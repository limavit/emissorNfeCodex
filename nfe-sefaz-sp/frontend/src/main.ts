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
              <label class="field required"><span>CNPJ</span><input [(ngModel)]="companyForm.cnpj" name="cnpj" required><small>Use o CNPJ da empresa emissora, preferencialmente somente numeros.</small></label>
              <label class="field required"><span>Razao social</span><input [(ngModel)]="companyForm.corporateName" name="corporateName" required><small>Informe o nome empresarial exatamente como consta no cadastro fiscal.</small></label>
              <label class="field"><span>Nome fantasia</span><input [(ngModel)]="companyForm.tradeName" name="tradeName"><small>Nome comercial usado no dia a dia, quando houver.</small></label>
              <label class="field"><span>Inscricao estadual</span><input [(ngModel)]="companyForm.stateRegistration" name="stateRegistration"><small>Obrigatoria para contribuintes de ICMS. Confirme com a empresa ou contador.</small></label>
              <label class="field required"><span>CNAE</span><input [(ngModel)]="companyForm.cnae" name="cnae" required><small>CNAE principal da empresa, sem pontuacao se possivel.</small></label>
              <label class="field required"><span>Regime tributario</span><select [(ngModel)]="companyForm.taxRegime" name="taxRegime" required><option>SIMPLES_NACIONAL</option><option>REGIME_NORMAL</option></select><small>Define quais regras fiscais serao esperadas nos produtos e notas.</small></label>
              <label class="field required"><span>CRT</span><input [(ngModel)]="companyForm.crt" name="crt" required><small>Codigo do regime tributario usado no XML da NF-e. Ex.: 1, 2 ou 3.</small></label>
              <label class="field required"><span>CEP</span><input [(ngModel)]="companyForm.zipCode" name="zipCode" required><small>Informe somente numeros para evitar rejeicoes de formato.</small></label>
              <label class="field required"><span>Logradouro</span><input [(ngModel)]="companyForm.street" name="street" required><small>Rua, avenida ou estrada do endereco fiscal do emitente.</small></label>
              <label class="field required"><span>Numero</span><input [(ngModel)]="companyForm.number" name="number" required><small>Use SN quando o endereco fiscal nao possuir numero.</small></label>
              <label class="field required"><span>Bairro</span><input [(ngModel)]="companyForm.district" name="district" required><small>Bairro do endereco fiscal.</small></label>
              <label class="field required"><span>Codigo IBGE</span><input [(ngModel)]="companyForm.cityCodeIbge" name="cityCodeIbge" required><small>Codigo de 7 digitos do municipio. Ex.: Sao Paulo/SP = 3550308.</small></label>
              <label class="field required"><span>Municipio</span><input [(ngModel)]="companyForm.cityName" name="cityName" required><small>Nome do municipio conforme tabela do IBGE.</small></label>
              <label class="field required"><span>UF</span><input [(ngModel)]="companyForm.uf" name="uf" required maxlength="2"><small>Sigla do estado com 2 letras. Para este projeto, normalmente SP.</small></label>
              <label class="field required"><span>Natureza padrao</span><input [(ngModel)]="companyForm.defaultNatureOperation" name="defaultNatureOperation" required><small>Descricao da operacao que costuma aparecer na NF-e. Ex.: Venda de mercadoria.</small></label>
              <label class="field required"><span>Indicador de presenca padrao</span><select [(ngModel)]="companyForm.defaultPresenceIndicator" name="defaultPresenceIndicator" required><option *ngFor="let option of presenceIndicatorOptions" [value]="option.code">{{ option.code }} - {{ option.label }}</option></select><small>Informa se a venda foi presencial, internet, teleatendimento ou outro canal.</small></label>
              <label class="field required"><span>Tipo de emissao padrao</span><select [(ngModel)]="companyForm.defaultEmissionType" name="defaultEmissionType" required><option *ngFor="let option of emissionTypeOptions" [value]="option.code">{{ option.code }} - {{ option.label }}</option></select><small>Use emissao normal salvo orientacao fiscal para contingencia.</small></label>
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
                <small>Selecione o certificado digital A1 da empresa emissora. Certificados A3 nao sao suportados nesta versao.</small>
              </label>
              <label class="field required">
                <span>Senha do certificado</span>
                <input [(ngModel)]="certificatePassword" name="certificatePassword" type="password" required autocomplete="new-password">
                <small>A senha e usada para validar o arquivo e sera armazenada criptografada.</small>
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
          <section *ngSwitchCase="'customers'">
            <crud-title title="Clientes"></crud-title>
            <div class="toolbar">
              <button (click)="openNewCustomer()">Novo cliente</button>
              <button class="secondary" (click)="loadCustomers()">Carregar clientes</button>
            </div>
            <p class="notice" *ngIf="!api.company()">Selecione uma empresa emissora antes de cadastrar clientes.</p>
            <p class="notice" *ngIf="customerMessage">{{ customerMessage }}</p>

            <form class="panel customer-form" *ngIf="showCustomerForm" (ngSubmit)="createCustomer()">
              <h2>Novo cliente</h2>
              <label class="field required">
                <span>Tipo de pessoa</span>
                <select [(ngModel)]="customerForm.personType" name="customerPersonType" required>
                  <option *ngFor="let option of personTypeOptions" [value]="option.code">{{ option.label }}</option>
                </select>
                <small>Define qual documento sera enviado no XML: CPF, CNPJ ou identificador estrangeiro.</small>
              </label>
              <label class="field required" *ngIf="customerForm.personType === 'FISICA'">
                <span>CPF</span>
                <input [(ngModel)]="customerForm.cpf" name="customerCpf" required maxlength="14">
                <small>Informe o CPF do destinatario, preferencialmente somente numeros.</small>
              </label>
              <label class="field required" *ngIf="customerForm.personType === 'JURIDICA'">
                <span>CNPJ</span>
                <input [(ngModel)]="customerForm.cnpj" name="customerCnpj" required maxlength="18">
                <small>Informe o CNPJ do destinatario, preferencialmente somente numeros.</small>
              </label>
              <label class="field required" *ngIf="customerForm.personType === 'ESTRANGEIRO'">
                <span>ID estrangeiro</span>
                <input [(ngModel)]="customerForm.foreignId" name="customerForeignId" required>
                <small>Identificacao fiscal ou documento usado no pais do destinatario.</small>
              </label>
              <label class="field required">
                <span>Nome ou razao social</span>
                <input [(ngModel)]="customerForm.name" name="customerName" required>
                <small>Nome que aparecera como destinatario da NF-e.</small>
              </label>
              <label class="field">
                <span>Nome fantasia</span>
                <input [(ngModel)]="customerForm.tradeName" name="customerTradeName">
                <small>Opcional para pessoa juridica; ajuda na identificacao interna.</small>
              </label>
              <label class="field required">
                <span>Indicador de inscricao estadual</span>
                <select [(ngModel)]="customerForm.stateRegistrationIndicator" name="customerIeIndicator" required>
                  <option *ngFor="let option of stateRegistrationIndicatorOptions" [value]="option.code">{{ option.label }}</option>
                </select>
                <small>Informe se o cliente e contribuinte de ICMS, isento ou nao contribuinte.</small>
              </label>
              <label class="field" *ngIf="customerForm.stateRegistrationIndicator === 'CONTRIBUINTE_ICMS'">
                <span>Inscricao estadual</span>
                <input [(ngModel)]="customerForm.stateRegistration" name="customerStateRegistration">
                <small>Obrigatoria quando o destinatario for contribuinte de ICMS.</small>
              </label>
              <label class="field">
                <span>Inscricao municipal</span>
                <input [(ngModel)]="customerForm.municipalRegistration" name="customerMunicipalRegistration">
                <small>Use quando existir no cadastro do cliente; nao substitui a inscricao estadual.</small>
              </label>
              <label class="field">
                <span>E-mail</span>
                <input [(ngModel)]="customerForm.email" name="customerEmail" type="email">
                <small>Contato para envio de XML/DANFE quando aplicavel.</small>
              </label>
              <label class="field">
                <span>Telefone</span>
                <input [(ngModel)]="customerForm.phone" name="customerPhone">
                <small>Telefone comercial ou do responsavel pelo recebimento.</small>
              </label>
              <label class="field required">
                <span>CEP</span>
                <input [(ngModel)]="customerForm.zipCode" name="customerZipCode" required maxlength="9">
                <small>Informe somente numeros para evitar rejeicoes de formato.</small>
              </label>
              <label class="field required">
                <span>Logradouro</span>
                <input [(ngModel)]="customerForm.street" name="customerStreet" required>
                <small>Rua, avenida, estrada ou equivalente do destinatario.</small>
              </label>
              <label class="field required">
                <span>Numero</span>
                <input [(ngModel)]="customerForm.number" name="customerNumber" required>
                <small>Use SN quando o endereco nao possuir numero.</small>
              </label>
              <label class="field">
                <span>Complemento</span>
                <input [(ngModel)]="customerForm.complement" name="customerComplement">
                <small>Opcional: sala, bloco, loja, apartamento ou referencia.</small>
              </label>
              <label class="field required">
                <span>Bairro</span>
                <input [(ngModel)]="customerForm.district" name="customerDistrict" required>
                <small>Bairro ou distrito do endereco do destinatario.</small>
              </label>
              <label class="field required">
                <span>Codigo IBGE do municipio</span>
                <input [(ngModel)]="customerForm.cityCodeIbge" name="customerCityCodeIbge" required maxlength="7">
                <small>Codigo oficial de 7 digitos do municipio conforme tabela IBGE.</small>
              </label>
              <label class="field required">
                <span>Municipio</span>
                <input [(ngModel)]="customerForm.cityName" name="customerCityName" required>
                <small>Nome do municipio do destinatario, conforme tabela IBGE.</small>
              </label>
              <label class="field required">
                <span>UF</span>
                <select [(ngModel)]="customerForm.uf" name="customerUf" required>
                  <option *ngFor="let uf of ufOptions" [value]="uf">{{ uf }}</option>
                </select>
                <small>Estado do destinatario. Para exterior, use EX.</small>
              </label>
              <label class="field required">
                <span>Codigo do pais</span>
                <input [(ngModel)]="customerForm.countryCode" name="customerCountryCode" required>
                <small>Brasil usa 1058. Para exterior, consulte a tabela fiscal vigente.</small>
              </label>
              <label class="field required">
                <span>Pais</span>
                <input [(ngModel)]="customerForm.countryName" name="customerCountryName" required>
                <small>Nome do pais do destinatario.</small>
              </label>
              <label class="field check">
                <input [(ngModel)]="customerForm.active" name="customerActive" type="checkbox">
                <span>Cliente ativo</span>
                <small>Clientes inativos nao devem ser usados em novas NF-e.</small>
              </label>
              <div class="form-actions">
                <button>Salvar cliente</button>
                <button type="button" class="secondary" (click)="showCustomerForm = false">Cancelar</button>
              </div>
            </form>
            <p class="required-legend" *ngIf="showCustomerForm"><span aria-hidden="true">*</span> campo obrigatorio</p>

            <div class="table-wrap" *ngIf="customerRows.length">
              <table>
                <thead>
                  <tr><th>Nome</th><th>Documento</th><th>IE</th><th>Municipio/UF</th><th>E-mail</th><th>Status</th></tr>
                </thead>
                <tbody>
                  <tr *ngFor="let customer of customerRows">
                    <td><strong>{{ customer.name }}</strong><br><span class="muted">{{ customer.tradeName || '-' }}</span></td>
                    <td>{{ customerDocument(customer) }}</td>
                    <td>{{ customer.stateRegistrationIndicator }}<br><span class="muted">{{ customer.stateRegistration || '-' }}</span></td>
                    <td>{{ customer.cityName || '-' }}/{{ customer.uf || '-' }}</td>
                    <td>{{ customer.email || '-' }}</td>
                    <td>{{ customer.active ? 'Ativo' : 'Inativo' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
          <section *ngSwitchCase="'products'"><crud-title title="Produtos"></crud-title><button (click)="load('/products')">Carregar produtos</button><pre>{{ rows | json }}</pre></section>
          <section *ngSwitchCase="'taxRules'"><h1>Regras fiscais do produto</h1><p>Cadastre CFOP, CST/CSOSN, ICMS, IPI, PIS e COFINS por UF, operacao e regime.</p></section>
          <section *ngSwitchCase="'nfe'">
            <h1>NF-e</h1>
            <div class="wizard-tabs">
              <button [class.active]="nfeStep === 1" (click)="nfeStep = 1">1. Dados gerais</button>
              <button [class.active]="nfeStep === 2" (click)="nfeStep = 2">2. Itens</button>
              <button [class.active]="nfeStep === 3" (click)="nfeStep = 3">3. Revisao</button>
            </div>

            <section class="panel" *ngIf="nfeStep === 1">
              <h2>Dados gerais</h2>
              <label class="field required"><span>Natureza da operacao</span><input [(ngModel)]="nfeForm.natureOperation" name="natureOperation" required><small>Texto que resume a operacao fiscal. Ex.: Venda de mercadoria adquirida de terceiros.</small></label>
              <label class="field required"><span>Tipo de operacao</span><select [(ngModel)]="nfeForm.operationType" name="operationType" required><option>SAIDA</option><option>ENTRADA</option></select><small>Saida para venda/remessa; entrada para compras, devolucoes recebidas ou ajustes.</small></label>
              <label class="field required"><span>Destino</span><select [(ngModel)]="nfeForm.destinationType" name="destinationType" required><option>INTERNA</option><option>INTERESTADUAL</option><option>EXTERIOR</option></select><small>Compare a UF do emitente com a UF do destinatario.</small></label>
              <label class="field required"><span>Finalidade</span><select [(ngModel)]="nfeForm.purpose" name="purpose" required><option>NORMAL</option><option>COMPLEMENTAR</option><option>AJUSTE</option><option>DEVOLUCAO</option></select><small>Use normal para emissao comum. As demais finalidades exigem validacao fiscal.</small></label>
              <label class="field required"><span>Indicador de presenca</span><select [(ngModel)]="nfeForm.presenceIndicator" name="presenceIndicator" required><option *ngFor="let option of presenceIndicatorOptions" [value]="option.code">{{ option.code }} - {{ option.label }}</option></select><small>Canal em que a venda ocorreu; o sistema enviara apenas o codigo selecionado.</small></label>
              <label class="field required"><span>Tipo de emissao</span><select [(ngModel)]="nfeForm.emissionType" name="emissionType" required><option *ngFor="let option of emissionTypeOptions" [value]="option.code">{{ option.code }} - {{ option.label }}</option></select><small>Use emissao normal quando a SEFAZ estiver disponivel.</small></label>
              <button type="button" (click)="nfeStep = 2">Avancar para itens</button>
            </section>

            <section *ngIf="nfeStep === 2">
              <form class="panel" (ngSubmit)="addNfeItem()">
                <h2>Adicionar item</h2>
                <label class="field required"><span>Codigo</span><input [(ngModel)]="nfeItemForm.productCode" name="productCode" required><small>Codigo interno ou SKU usado para identificar o produto.</small></label>
                <label class="field required"><span>Descricao</span><input [(ngModel)]="nfeItemForm.description" name="description" required><small>Descricao clara do item, como aparecera no documento fiscal.</small></label>
                <label class="field required"><span>NCM</span><input [(ngModel)]="nfeItemForm.ncm" name="ncm" required><small>Codigo fiscal de 8 digitos do produto. Confirme com o cadastro fiscal.</small></label>
                <label class="field required"><span>CFOP</span><input [(ngModel)]="nfeItemForm.cfop" name="cfop" required><small>Codigo da operacao fiscal. Ex.: venda dentro do estado, interestadual, devolucao.</small></label>
                <label class="field required"><span>Unidade comercial</span><input [(ngModel)]="nfeItemForm.commercialUnit" name="commercialUnit" required><small>Unidade de venda. Ex.: UN, CX, KG, M.</small></label>
                <label class="field required"><span>Quantidade</span><input [(ngModel)]="nfeItemForm.commercialQuantity" name="commercialQuantity" type="number" step="0.0001" required><small>Quantidade vendida na unidade comercial informada.</small></label>
                <label class="field required"><span>Valor unitario</span><input [(ngModel)]="nfeItemForm.commercialUnitValue" name="commercialUnitValue" type="number" step="0.0001" required><small>Valor de uma unidade antes de frete, seguro ou desconto.</small></label>
                <label class="field"><span>Frete</span><input [(ngModel)]="nfeItemForm.freightValue" name="freightValue" type="number" step="0.01"><small>Valor de frete rateado para este item, quando houver.</small></label>
                <label class="field"><span>Seguro</span><input [(ngModel)]="nfeItemForm.insuranceValue" name="insuranceValue" type="number" step="0.01"><small>Valor de seguro do item, quando houver.</small></label>
                <label class="field"><span>Desconto</span><input [(ngModel)]="nfeItemForm.discountValue" name="discountValue" type="number" step="0.01"><small>Desconto aplicado ao item.</small></label>
                <label class="field"><span>Outras despesas</span><input [(ngModel)]="nfeItemForm.otherExpenses" name="otherExpenses" type="number" step="0.01"><small>Despesas acessorias que compoem o valor da operacao.</small></label>
                <label class="field"><span>ICMS</span><input [(ngModel)]="nfeItemForm.icmsValue" name="icmsValue" type="number" step="0.01"><small>Valor do ICMS calculado conforme regra fiscal do produto.</small></label>
                <label class="field"><span>IPI</span><input [(ngModel)]="nfeItemForm.ipiValue" name="ipiValue" type="number" step="0.01"><small>Valor do IPI, quando aplicavel ao produto.</small></label>
                <label class="field"><span>PIS</span><input [(ngModel)]="nfeItemForm.pisValue" name="pisValue" type="number" step="0.01"><small>Valor do PIS conforme configuracao fiscal.</small></label>
                <label class="field"><span>COFINS</span><input [(ngModel)]="nfeItemForm.cofinsValue" name="cofinsValue" type="number" step="0.01"><small>Valor da COFINS conforme configuracao fiscal.</small></label>
                <label class="field check"><input [(ngModel)]="nfeItemForm.includeInTotal" name="includeInTotal" type="checkbox"><span>Compoe total da NF-e</span></label>
                <button>Adicionar item</button>
              </form>
              <p class="required-legend"><span aria-hidden="true">*</span> campo obrigatorio</p>

              <div class="table-wrap" *ngIf="nfeItems.length">
                <table>
                  <thead><tr><th>#</th><th>Codigo</th><th>Descricao</th><th>NCM</th><th>CFOP</th><th>Total</th><th></th></tr></thead>
                  <tbody>
                    <tr *ngFor="let item of nfeItems; let i = index">
                      <td>{{ i + 1 }}</td>
                      <td>{{ item.productCode }}</td>
                      <td>{{ item.description }}</td>
                      <td>{{ item.ncm }}</td>
                      <td>{{ item.cfop }}</td>
                      <td>{{ item.grossTotal | currency:'BRL' }}</td>
                      <td><button class="danger-outline" (click)="removeNfeItem(i)">Remover</button></td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <button type="button" (click)="nfeStep = 3" [disabled]="!nfeItems.length">Revisar totais</button>
            </section>

            <section *ngIf="nfeStep === 3">
              <h2>Revisao e totais</h2>
              <div class="metrics">
                <article><span>Produtos</span><strong>{{ nfeTotals.products | currency:'BRL' }}</strong></article>
                <article><span>Frete</span><strong>{{ nfeTotals.freight | currency:'BRL' }}</strong></article>
                <article><span>Seguro</span><strong>{{ nfeTotals.insurance | currency:'BRL' }}</strong></article>
                <article><span>Desconto</span><strong>{{ nfeTotals.discount | currency:'BRL' }}</strong></article>
                <article><span>Outras despesas</span><strong>{{ nfeTotals.other | currency:'BRL' }}</strong></article>
                <article><span>IPI</span><strong>{{ nfeTotals.ipi | currency:'BRL' }}</strong></article>
                <article><span>Total NF-e</span><strong>{{ nfeTotals.invoice | currency:'BRL' }}</strong></article>
              </div>
              <div class="toolbar">
                <button (click)="submitNfeWizard()" [disabled]="!nfeItems.length">Criar rascunho com itens</button>
                <button (click)="load('/nfe')">Listar NF-e</button>
              </div>
              <pre>{{ rows | json }}</pre>
            </section>
          </section>
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
  customerRows: any[] = [];
  showCustomerForm = false;
  customerMessage = '';
  selectedCertificateFile: File | null = null;
  certificatePassword = '';
  certificateStatus: any = null;
  certificateMessage = '';
  presenceIndicatorOptions = [
    { code: '0', label: 'Nao se aplica' },
    { code: '1', label: 'Operacao presencial' },
    { code: '2', label: 'Operacao pela internet' },
    { code: '3', label: 'Operacao por teleatendimento' },
    { code: '4', label: 'NFC-e com entrega em domicilio' },
    { code: '5', label: 'Operacao presencial fora do estabelecimento' },
    { code: '9', label: 'Outros' }
  ];
  emissionTypeOptions = [
    { code: '1', label: 'Emissao normal' },
    { code: '2', label: 'Contingencia FS-IA' },
    { code: '4', label: 'Contingencia EPEC' },
    { code: '5', label: 'Contingencia FS-DA' },
    { code: '6', label: 'Contingencia SVC-AN' },
    { code: '7', label: 'Contingencia SVC-RS' },
    { code: '9', label: 'Contingencia off-line NFC-e' }
  ];
  personTypeOptions = [
    { code: 'JURIDICA', label: 'Pessoa juridica' },
    { code: 'FISICA', label: 'Pessoa fisica' },
    { code: 'ESTRANGEIRO', label: 'Estrangeiro' }
  ];
  stateRegistrationIndicatorOptions = [
    { code: 'CONTRIBUINTE_ICMS', label: 'Contribuinte ICMS' },
    { code: 'ISENTO', label: 'Contribuinte isento' },
    { code: 'NAO_CONTRIBUINTE', label: 'Nao contribuinte' }
  ];
  ufOptions = ['SP', 'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SE', 'TO', 'EX'];
  customerForm: any = this.emptyCustomer();
  nfeStep = 1;
  nfeForm: any = {
    natureOperation: '',
    operationType: 'SAIDA',
    destinationType: 'INTERNA',
    purpose: 'NORMAL',
    presenceIndicator: '9',
    emissionType: '1'
  };
  nfeItemForm: any = this.emptyNfeItem();
  nfeItems: any[] = [];
  nfeTotals = this.calculateNfeTotals();
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

  emptyCustomer() {
    return {
      personType: 'JURIDICA',
      cpf: '',
      cnpj: '',
      foreignId: '',
      name: '',
      tradeName: '',
      stateRegistrationIndicator: 'NAO_CONTRIBUINTE',
      stateRegistration: '',
      municipalRegistration: '',
      email: '',
      phone: '',
      zipCode: '',
      street: '',
      number: '',
      complement: '',
      district: '',
      cityCodeIbge: '',
      cityName: '',
      uf: 'SP',
      countryCode: '1058',
      countryName: 'Brasil',
      active: true
    };
  }

  openNewCustomer() {
    this.customerForm = this.emptyCustomer();
    this.customerMessage = '';
    this.showCustomerForm = true;
  }

  loadCustomers() {
    const company = this.api.company();
    if (!company) {
      this.customerMessage = 'Selecione uma empresa antes de consultar clientes.';
      return;
    }
    this.api.get(`/api/companies/${company.id}/customers`).subscribe({
      next: customers => {
        this.customerRows = customers;
        this.customerMessage = customers.length ? '' : 'Nenhum cliente cadastrado para esta empresa.';
      },
      error: error => this.customerMessage = error.error?.message || 'Nao foi possivel carregar clientes.'
    });
  }

  createCustomer() {
    const company = this.api.company();
    if (!company) {
      this.customerMessage = 'Selecione uma empresa antes de cadastrar clientes.';
      return;
    }
    const message = this.validateCustomerForm();
    if (message) {
      this.customerMessage = message;
      return;
    }
    const payload = this.normalizeCustomerPayload();
    this.api.post(`/api/companies/${company.id}/customers`, payload).subscribe({
      next: customer => {
        this.customerRows = [customer, ...this.customerRows.filter(row => row.id !== customer.id)];
        this.customerForm = this.emptyCustomer();
        this.showCustomerForm = false;
        this.customerMessage = 'Cliente cadastrado com sucesso.';
      },
      error: error => this.customerMessage = error.error?.message || 'Nao foi possivel cadastrar o cliente.'
    });
  }

  validateCustomerForm() {
    const required = ['name', 'zipCode', 'street', 'number', 'district', 'cityCodeIbge', 'cityName', 'uf', 'countryCode', 'countryName'];
    if (required.some(field => !String(this.customerForm[field] || '').trim())) {
      return 'Preencha os campos obrigatorios do cliente.';
    }
    if (this.customerForm.personType === 'JURIDICA' && !this.digits(this.customerForm.cnpj)) {
      return 'Informe o CNPJ do cliente pessoa juridica.';
    }
    if (this.customerForm.personType === 'FISICA' && !this.digits(this.customerForm.cpf)) {
      return 'Informe o CPF do cliente pessoa fisica.';
    }
    if (this.customerForm.personType === 'ESTRANGEIRO' && !String(this.customerForm.foreignId || '').trim()) {
      return 'Informe o ID estrangeiro do cliente.';
    }
    if (this.customerForm.stateRegistrationIndicator === 'CONTRIBUINTE_ICMS' && !String(this.customerForm.stateRegistration || '').trim()) {
      return 'Informe a inscricao estadual para contribuinte ICMS.';
    }
    return '';
  }

  normalizeCustomerPayload() {
    return {
      ...this.customerForm,
      cpf: this.customerForm.personType === 'FISICA' ? this.digits(this.customerForm.cpf) : '',
      cnpj: this.customerForm.personType === 'JURIDICA' ? this.digits(this.customerForm.cnpj) : '',
      foreignId: this.customerForm.personType === 'ESTRANGEIRO' ? String(this.customerForm.foreignId || '').trim() : '',
      zipCode: this.digits(this.customerForm.zipCode),
      cityCodeIbge: this.digits(this.customerForm.cityCodeIbge),
      uf: String(this.customerForm.uf || '').toUpperCase(),
      countryCode: this.digits(this.customerForm.countryCode) || this.customerForm.countryCode
    };
  }

  customerDocument(customer: any) {
    if (customer.personType === 'FISICA') return customer.cpf || '-';
    if (customer.personType === 'ESTRANGEIRO') return customer.foreignId || '-';
    return customer.cnpj || '-';
  }

  digits(value: any) {
    return String(value || '').replace(/\D/g, '');
  }

  createNfe() {
    const company = this.api.company();
    if (!company) return;
    this.api.post(`/api/companies/${company.id}/nfe`, { natureOperation: company.defaultNatureOperation }).subscribe(() => this.load('/nfe'));
  }

  emptyNfeItem() {
    return {
      productCode: '',
      description: '',
      ncm: '',
      cfop: '',
      commercialUnit: 'UN',
      commercialQuantity: 1,
      commercialUnitValue: 0,
      freightValue: 0,
      insuranceValue: 0,
      discountValue: 0,
      otherExpenses: 0,
      icmsValue: 0,
      ipiValue: 0,
      pisValue: 0,
      cofinsValue: 0,
      includeInTotal: true
    };
  }

  addNfeItem() {
    const quantity = this.number(this.nfeItemForm.commercialQuantity);
    const unitValue = this.number(this.nfeItemForm.commercialUnitValue);
    if (!this.nfeItemForm.productCode || !this.nfeItemForm.description || !this.nfeItemForm.ncm || !this.nfeItemForm.cfop || quantity <= 0 || unitValue <= 0) {
      return;
    }
    const item = {
      ...this.nfeItemForm,
      commercialQuantity: quantity,
      commercialUnitValue: unitValue,
      grossTotal: this.money(quantity * unitValue),
      taxableUnit: this.nfeItemForm.commercialUnit,
      taxableQuantity: quantity,
      taxableUnitValue: unitValue,
      freightValue: this.money(this.nfeItemForm.freightValue),
      insuranceValue: this.money(this.nfeItemForm.insuranceValue),
      discountValue: this.money(this.nfeItemForm.discountValue),
      otherExpenses: this.money(this.nfeItemForm.otherExpenses),
      icmsValue: this.money(this.nfeItemForm.icmsValue),
      ipiValue: this.money(this.nfeItemForm.ipiValue),
      pisValue: this.money(this.nfeItemForm.pisValue),
      cofinsValue: this.money(this.nfeItemForm.cofinsValue)
    };
    this.nfeItems = [...this.nfeItems, item];
    this.nfeItemForm = this.emptyNfeItem();
    this.nfeTotals = this.calculateNfeTotals();
  }

  removeNfeItem(index: number) {
    this.nfeItems = this.nfeItems.filter((_, i) => i !== index);
    this.nfeTotals = this.calculateNfeTotals();
  }

  calculateNfeTotals() {
    const totals = this.nfeItems.reduce((acc, item) => {
      if (item.includeInTotal !== false) acc.products += this.number(item.grossTotal);
      acc.freight += this.number(item.freightValue);
      acc.insurance += this.number(item.insuranceValue);
      acc.discount += this.number(item.discountValue);
      acc.other += this.number(item.otherExpenses);
      acc.icms += this.number(item.icmsValue);
      acc.ipi += this.number(item.ipiValue);
      acc.pis += this.number(item.pisValue);
      acc.cofins += this.number(item.cofinsValue);
      return acc;
    }, { products: 0, freight: 0, insurance: 0, discount: 0, other: 0, icms: 0, ipi: 0, pis: 0, cofins: 0, invoice: 0 });
    totals.invoice = this.money(totals.products + totals.freight + totals.insurance + totals.other + totals.ipi - totals.discount);
    Object.keys(totals).forEach(key => totals[key] = this.money(totals[key]));
    return totals;
  }

  submitNfeWizard() {
    const company = this.api.company();
    if (!company || !this.nfeItems.length) return;
    const payload = {
      ...this.nfeForm,
      natureOperation: this.nfeForm.natureOperation || company.defaultNatureOperation,
      items: this.nfeItems
    };
    this.api.post(`/api/companies/${company.id}/nfe`, payload).subscribe(created => {
      this.rows = [created];
      this.nfeItems = [];
      this.nfeTotals = this.calculateNfeTotals();
      this.nfeItemForm = this.emptyNfeItem();
      this.nfeStep = 1;
    });
  }

  number(value: any) {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : 0;
  }

  money(value: any) {
    return Math.round(this.number(value) * 100) / 100;
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
