package com.example.nfe.company;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "owner_user_id")
    private UUID ownerUserId;
    private String cnpj;
    @Column(name = "corporate_name")
    private String corporateName;
    @Column(name = "trade_name")
    private String tradeName;
    @Column(name = "state_registration")
    private String stateRegistration;
    @Column(name = "municipal_registration")
    private String municipalRegistration;
    private String cnae;
    @Column(name = "tax_regime")
    private String taxRegime;
    private String crt;
    @Column(name = "zip_code")
    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String district;
    @Column(name = "city_code_ibge")
    private String cityCodeIbge;
    @Column(name = "city_name")
    private String cityName;
    @Column(length = 2)
    private String uf;
    @Column(name = "country_code")
    private String countryCode = "1058";
    @Column(name = "country_name")
    private String countryName = "Brasil";
    private String phone;
    private String email;
    private String environment = "HOMOLOGACAO";
    @Column(name = "default_series")
    private Integer defaultSeries = 1;
    @Column(name = "next_nfe_number")
    private Long nextNfeNumber = 1L;
    @Column(name = "default_nature_operation")
    private String defaultNatureOperation;
    @Column(name = "default_presence_indicator")
    private String defaultPresenceIndicator = "9";
    @Column(name = "default_emission_type")
    private String defaultEmissionType = "1";
    private boolean active = true;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected Company() {
    }

    public static Company from(UUID ownerUserId, CompanyRequest request) {
        Company c = new Company();
        c.ownerUserId = ownerUserId;
        c.apply(request);
        c.environment = request.environment() == null ? "HOMOLOGACAO" : request.environment();
        return c;
    }

    public void apply(CompanyRequest request) {
        this.cnpj = request.cnpj();
        this.corporateName = request.corporateName();
        this.tradeName = request.tradeName();
        this.stateRegistration = request.stateRegistration();
        this.municipalRegistration = request.municipalRegistration();
        this.cnae = request.cnae();
        this.taxRegime = request.taxRegime();
        this.crt = request.crt();
        this.zipCode = request.zipCode();
        this.street = request.street();
        this.number = request.number();
        this.complement = request.complement();
        this.district = request.district();
        this.cityCodeIbge = request.cityCodeIbge();
        this.cityName = request.cityName();
        this.uf = request.uf();
        this.countryCode = request.countryCode() == null ? "1058" : request.countryCode();
        this.countryName = request.countryName() == null ? "Brasil" : request.countryName();
        this.phone = request.phone();
        this.email = request.email();
        this.defaultSeries = request.defaultSeries() == null ? 1 : request.defaultSeries();
        this.nextNfeNumber = request.nextNfeNumber() == null ? 1L : request.nextNfeNumber();
        this.defaultNatureOperation = request.defaultNatureOperation();
        this.defaultPresenceIndicator = request.defaultPresenceIndicator() == null ? "9" : request.defaultPresenceIndicator();
        this.defaultEmissionType = request.defaultEmissionType() == null ? "1" : request.defaultEmissionType();
        this.active = request.active() == null || request.active();
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getOwnerUserId() { return ownerUserId; }
    public String getCnpj() { return cnpj; }
    public String getCorporateName() { return corporateName; }
    public String getTradeName() { return tradeName; }
    public String getEnvironment() { return environment; }
    public Integer getDefaultSeries() { return defaultSeries; }
    public Long getNextNfeNumber() { return nextNfeNumber; }
    public boolean isActive() { return active; }
}
