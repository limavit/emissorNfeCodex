import 'zone.js';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders, provideHttpClient } from '@angular/common/http';
import { Component, Injectable, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { bootstrapApplication } from '@angular/platform-browser';

@Injectable({ providedIn: 'root' })
class Api {
  token = signal(localStorage.getItem('token') || '');
  company = signal<any>(JSON.parse(localStorage.getItem('company') || 'null'));

  constructor(private http: HttpClient) {}

  headers() {
    return { headers: new HttpHeaders({ Authorization: `Bearer ${this.token()}` }) };
  }

  login(email: string, password: string) {
    return this.http.post<any>('/api/auth/login', { email, password });
  }

  register(name: string, email: string, password: string) {
    return this.http.post<any>('/api/auth/register', { name, email, password });
  }

  get(path: string) { return this.http.get<any>(path, this.headers()); }
  post(path: string, body: any) { return this.http.post<any>(path, body, this.headers()); }
  postForm(path: string, body: FormData) { return this.http.post<any>(path, body, this.headers()); }
  delete(path: string) { return this.http.delete<any>(path, this.headers()); }

  setSession(response: any) {
    localStorage.setItem('token', response.token);
    this.token.set(response.token);
  }

  selectCompany(company: any) {
    localStorage.setItem('company', JSON.stringify(company));
    this.company.set(company);
  }
}

@Component({
  selector: 'crud-title',
  standalone: true,
  template: `<h1>{{ title }}</h1><p class="notice">Dados sempre filtrados pela empresa selecionada.</p>`,
  inputs: ['title']
})
class CrudTitleComponent {
  title = '';
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, CrudTitleComponent],
  template: `
    <main class="shell" *ngIf="api.token(); else auth">
      <aside>
        <strong>NF-e SP</strong>
        <button (click)="screen='companies'">Empresas</button>
        <button (click)="screen='dashboard'">Dashboard</button>
        <button (click)="screen='certificate'">Certificado</button>
        <button (click)="screen='customers'">Clientes</button>
        <button (click)="screen='products'">Produtos</button>
        <button (click)="screen='taxRules'">Regras fiscais</button>
        <button (click)="screen='nfe'">NF-e</button>
        <button (click)="screen='logs'">Logs</button>
        <button (click)="screen='audit'">Auditoria</button>
        <button class="danger" (click)="logout()">Sair</button>
      </aside>

      <section class="content">
        <div class="production" *ngIf="api.company()?.environment === 'PRODUCAO'">AMBIENTE DE PRODUCAO ATIVO</div>

        <ng-container [ngSwitch]="screen">
          <section *ngSwitchCase="'companies'">
            <h1>Selecionar Empresa Emissora</h1>
            <p *ngIf="companies.length === 0" class="notice">Cadastre uma empresa emissora para começar a emitir NF-e.</p>
            <div class="grid">
              <article class="card" *ngFor="let c of companies">
                <h2>{{ c.corporateName }}</h2>
                <p>{{ c.tradeName }} - {{ c.cnpj }}</p>
                <p>Ambiente: {{ c.environment }} | Serie {{ c.defaultSeries }} | Proxima {{ c.nextNfeNumber }}</p>
                <button (click)="select(c)">Selecionar CNPJ</button>
              </article>
            </div>
            <form class="panel" (ngSubmit)="createCompany()">
              <h2>Cadastro de empresa</h2>
              <label class="field required"><span>CNPJ</span><input [(ngModel)]="companyForm.cnpj" name="cnpj" required></label>
              <label class="field required"><span>Razao social</span><input [(ngModel)]="companyForm.corporateName" name="corporateName" required></label>
              <label class="field"><span>Nome fantasia</span><input [(ngModel)]="companyForm.tradeName" name="tradeName"></label>
              <label class="field"><span>Inscricao estadual</span><input [(ngModel)]="companyForm.stateRegistration" name="stateRegistration"></label>
              <label class="field required"><span>CNAE</span><input [(ngModel)]="companyForm.cnae" name="cnae" required></label>
              <label class="field required"><span>Regime tributario</span><select [(ngModel)]="companyForm.taxRegime" name="taxRegime" required><option>SIMPLES_NACIONAL</option><option>REGIME_NORMAL</option></select></label>
              <label class="field required"><span>CRT</span><input [(ngModel)]="companyForm.crt" name="crt" required></label>
              <label class="field required"><span>CEP</span><input [(ngModel)]="companyForm.zipCode" name="zipCode" required></label>
              <label class="field required"><span>Logradouro</span><input [(ngModel)]="companyForm.street" name="street" required></label>
              <label class="field required"><span>Numero</span><input [(ngModel)]="companyForm.number" name="number" required></label>
              <label class="field required"><span>Bairro</span><input [(ngModel)]="companyForm.district" name="district" required></label>
              <label class="field required"><span>Codigo IBGE</span><input [(ngModel)]="companyForm.cityCodeIbge" name="cityCodeIbge" required></label>
              <label class="field required"><span>Municipio</span><input [(ngModel)]="companyForm.cityName" name="cityName" required></label>
              <label class="field required"><span>UF</span><input [(ngModel)]="companyForm.uf" name="uf" required maxlength="2"></label>
              <label class="field required"><span>Natureza padrao</span><input [(ngModel)]="companyForm.defaultNatureOperation" name="defaultNatureOperation" required></label>
              <button>Cadastrar empresa</button>
            </form>
            <p class="required-legend"><span aria-hidden="true">*</span> campo obrigatorio</p>
          </section>

          <section *ngSwitchCase="'dashboard'">
            <h1>Dashboard</h1>
            <div class="metrics">
              <article><span>Empresa</span><strong>{{ api.company()?.corporateName || 'Nao selecionada' }}</strong></article>
              <article><span>CNPJ</span><strong>{{ api.company()?.cnpj || '-' }}</strong></article>
              <article><span>Ambiente</span><strong>{{ api.company()?.environment || '-' }}</strong></article>
              <article><span>Certificado</span><strong>Ausente</strong></article>
              <article><span>Serie</span><strong>{{ api.company()?.defaultSeries || '-' }}</strong></article>
              <article><span>Proxima NF-e</span><strong>{{ api.company()?.nextNfeNumber || '-' }}</strong></article>
            </div>
            <p class="notice">O sistema nao inventa tributacao. Valide regras fiscais com contador antes de producao.</p>
          </section>

          <section *ngSwitchCase="'certificate'">
            <h1>Certificado A1</h1>
            <p class="notice">Upload .pfx/.p12 com senha. Senhas nunca devem aparecer em logs ou respostas.</p>
            <div class="toolbar">
              <button (click)="loadCertificateStatus()">Atualizar status</button>
              <button class="danger-outline" (click)="removeCertificate()" [disabled]="!api.company()">Remover certificado</button>
            </div>
            <section class="panel certificate-panel">
              <h2>Enviar certificado</h2>
              <label class="field required">
                <span>Arquivo A1 (.pfx ou .p12)</span>
                <input type="file" accept=".pfx,.p12" required (change)="selectCertificateFile($any($event.target).files?.[0])">
              </label>
              <label class="field required">
                <span>Senha do certificado</span>
                <input [(ngModel)]="certificatePassword" name="certificatePassword" type="password" required autocomplete="new-password">
              </label>
              <button (click)="uploadCertificate()" [disabled]="!api.company()">Enviar e validar</button>
            </section>
            <p class="required-legend"><span aria-hidden="true">*</span> campo obrigatorio</p>
            <article class="card certificate-status" *ngIf="certificateStatus">
              <h2>Status do certificado</h2>
              <p><strong>{{ certificateStatus.status }}</strong></p>
              <p *ngIf="certificateStatus.subjectName">Titular: {{ certificateStatus.subjectName }}</p>
              <p *ngIf="certificateStatus.issuerName">Emissor: {{ certificateStatus.issuerName }}</p>
              <p *ngIf="certificateStatus.serialNumber">Serial: {{ certificateStatus.serialNumber }}</p>
              <p *ngIf="certificateStatus.certificateDocument">Documento: {{ certificateStatus.certificateDocument }}</p>
              <p *ngIf="certificateStatus.validFrom">Valido de: {{ certificateStatus.validFrom | date:'short' }}</p>
              <p *ngIf="certificateStatus.validUntil">Valido ate: {{ certificateStatus.validUntil | date:'short' }}</p>
            </article>
            <p class="notice" *ngIf="certificateMessage">{{ certificateMessage }}</p>
          </section>
          <section *ngSwitchCase="'customers'"><crud-title title="Clientes"></crud-title><button (click)="load('/customers')">Carregar clientes</button><pre>{{ rows | json }}</pre></section>
          <section *ngSwitchCase="'products'"><crud-title title="Produtos"></crud-title><button (click)="load('/products')">Carregar produtos</button><pre>{{ rows | json }}</pre></section>
          <section *ngSwitchCase="'taxRules'"><h1>Regras fiscais do produto</h1><p>Cadastre CFOP, CST/CSOSN, ICMS, IPI, PIS e COFINS por UF, operacao e regime.</p></section>
          <section *ngSwitchCase="'nfe'"><h1>NF-e</h1><button (click)="createNfe()">Criar rascunho</button><button (click)="load('/nfe')">Listar NF-e</button><pre>{{ rows | json }}</pre></section>
          <section *ngSwitchCase="'logs'"><h1>Logs SEFAZ</h1><button (click)="load('/sefaz-logs')">Carregar</button><pre>{{ rows | json }}</pre></section>
          <section *ngSwitchCase="'audit'"><h1>Auditoria</h1><button (click)="load('/audit-logs')">Carregar</button><pre>{{ rows | json }}</pre></section>
        </ng-container>
      </section>
    </main>

    <ng-template #auth>
      <section class="auth">
        <form (ngSubmit)="submitAuth()">
          <h1>NF-e SEFAZ-SP</h1>
          <label class="field required" *ngIf="mode === 'register'"><span>Nome</span><input [(ngModel)]="authForm.name" name="name" required></label>
          <label class="field required"><span>E-mail</span><input [(ngModel)]="authForm.email" name="email" type="email" required></label>
          <label class="field required"><span>Senha</span><input [(ngModel)]="authForm.password" name="password" type="password" required></label>
          <button>{{ mode === 'login' ? 'Entrar' : 'Cadastrar' }}</button>
          <a (click)="mode = mode === 'login' ? 'register' : 'login'">{{ mode === 'login' ? 'Criar conta' : 'Ja tenho conta' }}</a>
          <p class="required-legend"><span aria-hidden="true">*</span> campo obrigatorio</p>
        </form>
      </section>
    </ng-template>
  `
})
class AppComponent {
  screen = 'companies';
  mode: 'login' | 'register' = 'login';
  companies: any[] = [];
  rows: any[] = [];
  selectedCertificateFile: File | null = null;
  certificatePassword = '';
  certificateStatus: any = null;
  certificateMessage = '';
  authForm = { name: '', email: '', password: '' };
  companyForm: any = {
    taxRegime: 'SIMPLES_NACIONAL', countryCode: '1058', countryName: 'Brasil',
    environment: 'HOMOLOGACAO', defaultSeries: 1, nextNfeNumber: 1,
    defaultPresenceIndicator: '9', defaultEmissionType: '1', active: true
  };

  constructor(public api: Api) {
    if (api.token()) this.refreshCompanies();
  }

  submitAuth() {
    const call = this.mode === 'login'
      ? this.api.login(this.authForm.email, this.authForm.password)
      : this.api.register(this.authForm.name, this.authForm.email, this.authForm.password);
    call.subscribe(r => { this.api.setSession(r); this.refreshCompanies(); });
  }

  refreshCompanies() {
    this.api.get('/api/companies').subscribe(r => this.companies = r);
  }

  createCompany() {
    this.api.post('/api/companies', this.companyForm).subscribe(c => { this.refreshCompanies(); this.select(c); });
  }

  select(c: any) {
    this.api.post(`/api/companies/${c.id}/select`, {}).subscribe(company => {
      this.api.selectCompany(company);
      this.loadCertificateStatus();
      this.screen = 'dashboard';
    });
  }

  load(path: string) {
    const company = this.api.company();
    if (!company) return;
    this.api.get(`/api/companies/${company.id}${path}`).subscribe(r => this.rows = r);
  }

  createNfe() {
    const company = this.api.company();
    if (!company) return;
    this.api.post(`/api/companies/${company.id}/nfe`, { natureOperation: company.defaultNatureOperation }).subscribe(() => this.load('/nfe'));
  }

  selectCertificateFile(file: File | null) {
    this.selectedCertificateFile = file;
  }

  loadCertificateStatus() {
    const company = this.api.company();
    if (!company) {
      this.certificateMessage = 'Selecione uma empresa antes de gerenciar certificado.';
      return;
    }
    this.api.get(`/api/companies/${company.id}/certificate`).subscribe({
      next: status => {
        this.certificateStatus = status;
        this.certificateMessage = status.status === 'AUSENTE' ? 'Nenhum certificado ativo cadastrado.' : '';
      },
      error: error => this.certificateMessage = error.error?.message || 'Nao foi possivel consultar o certificado.'
    });
  }

  uploadCertificate() {
    const company = this.api.company();
    if (!company) {
      this.certificateMessage = 'Selecione uma empresa antes de enviar certificado.';
      return;
    }
    if (!this.selectedCertificateFile || !this.certificatePassword) {
      this.certificateMessage = 'Informe arquivo e senha do certificado.';
      return;
    }
    const form = new FormData();
    form.append('file', this.selectedCertificateFile);
    form.append('password', this.certificatePassword);
    this.api.postForm(`/api/companies/${company.id}/certificate`, form).subscribe({
      next: status => {
        this.certificateStatus = status;
        this.certificatePassword = '';
        this.certificateMessage = 'Certificado validado e armazenado com criptografia.';
      },
      error: error => this.certificateMessage = error.error?.message || 'Nao foi possivel validar o certificado.'
    });
  }

  removeCertificate() {
    const company = this.api.company();
    if (!company) return;
    this.api.delete(`/api/companies/${company.id}/certificate`).subscribe({
      next: () => {
        this.certificateStatus = { companyId: company.id, status: 'AUSENTE' };
        this.certificateMessage = 'Certificado removido.';
      },
      error: error => this.certificateMessage = error.error?.message || 'Nao foi possivel remover o certificado.'
    });
  }

  logout() {
    localStorage.clear();
    this.api.token.set('');
    this.api.company.set(null);
  }
}

bootstrapApplication(AppComponent, { providers: [provideHttpClient()] });
