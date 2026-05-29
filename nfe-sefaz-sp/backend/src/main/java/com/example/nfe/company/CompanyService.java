package com.example.nfe.company;

import com.example.nfe.audit.AuditService;
import com.example.nfe.common.CnpjValidator;
import com.example.nfe.security.CurrentUser;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {
    private final CompanyRepository companies;
    private final AuditService auditService;

    public CompanyService(CompanyRepository companies, AuditService auditService) {
        this.companies = companies;
        this.auditService = auditService;
    }

    public List<Company> list() {
        return companies.findByOwnerUserIdOrderByCorporateName(CurrentUser.id());
    }

    public Company get(UUID id) {
        return companies.findByIdAndOwnerUserId(id, CurrentUser.id()).orElseThrow(() -> new IllegalArgumentException("Empresa nao encontrada."));
    }

    @Transactional
    public Company create(CompanyRequest request) {
        validate(request);
        UUID userId = CurrentUser.id();
        String cnpj = CnpjValidator.digits(request.cnpj());
        if (companies.existsByOwnerUserIdAndCnpj(userId, cnpj)) {
            throw new IllegalArgumentException("CNPJ ja cadastrado para este usuario.");
        }
        Company company = companies.save(Company.from(userId, normalize(request, cnpj)));
        auditService.register(userId, company.getId(), "CREATE_COMPANY", "Company", company.getId().toString());
        return company;
    }

    @Transactional
    public Company update(UUID id, CompanyRequest request) {
        validate(request);
        Company company = get(id);
        company.apply(normalize(request, CnpjValidator.digits(request.cnpj())));
        auditService.register(CurrentUser.id(), company.getId(), "UPDATE_COMPANY", "Company", company.getId().toString());
        return company;
    }

    public Company select(UUID id) {
        Company company = get(id);
        auditService.register(CurrentUser.id(), company.getId(), "SELECT_COMPANY", "Company", company.getId().toString());
        return company;
    }

    private void validate(CompanyRequest request) {
        if (!CnpjValidator.isValid(request.cnpj())) {
            throw new IllegalArgumentException("CNPJ invalido.");
        }
        if (!request.uf().matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("UF invalida.");
        }
        if (!request.cityCodeIbge().matches("\\d{7}")) {
            throw new IllegalArgumentException("Codigo IBGE do municipio invalido.");
        }
    }

    private CompanyRequest normalize(CompanyRequest r, String cnpj) {
        return new CompanyRequest(cnpj, r.corporateName(), r.tradeName(), r.stateRegistration(), r.municipalRegistration(),
                r.cnae(), r.taxRegime(), r.crt(), r.zipCode(), r.street(), r.number(), r.complement(), r.district(),
                r.cityCodeIbge(), r.cityName(), r.uf(), r.countryCode(), r.countryName(), r.phone(), r.email(),
                r.environment(), r.defaultSeries(), r.nextNfeNumber(), r.defaultNatureOperation(),
                r.defaultPresenceIndicator(), r.defaultEmissionType(), r.active());
    }
}
