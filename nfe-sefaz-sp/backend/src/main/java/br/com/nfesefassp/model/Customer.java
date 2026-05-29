package br.com.nfesefassp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "company_id")
    private UUID companyId;
    @Column(name = "person_type")
    private String personType;
    private String cpf;
    private String cnpj;
    @Column(name = "foreign_id")
    private String foreignId;
    private String name;
    @Column(name = "trade_name")
    private String tradeName;
    @Column(name = "state_registration_indicator")
    private String stateRegistrationIndicator;
    @Column(name = "state_registration")
    private String stateRegistration;
    @Column(name = "municipal_registration")
    private String municipalRegistration;
    private String email;
    private String phone;
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
    private boolean active = true;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected Customer() {
    }

    public static Customer from(UUID companyId, CustomerRequest request) {
        Customer customer = new Customer();
        customer.companyId = companyId;
        customer.apply(request);
        return customer;
    }

    public void apply(CustomerRequest request) {
        this.personType = valueOrDefault(request.personType(), "JURIDICA");
        this.cpf = request.cpf();
        this.cnpj = request.cnpj();
        this.foreignId = request.foreignId();
        this.name = request.name();
        this.tradeName = request.tradeName();
        this.stateRegistrationIndicator = valueOrDefault(request.stateRegistrationIndicator(), "NAO_CONTRIBUINTE");
        this.stateRegistration = request.stateRegistration();
        this.municipalRegistration = request.municipalRegistration();
        this.email = request.email();
        this.phone = request.phone();
        this.zipCode = request.zipCode();
        this.street = request.street();
        this.number = request.number();
        this.complement = request.complement();
        this.district = request.district();
        this.cityCodeIbge = request.cityCodeIbge();
        this.cityName = request.cityName();
        this.uf = request.uf();
        this.countryCode = valueOrDefault(request.countryCode(), "1058");
        this.countryName = valueOrDefault(request.countryName(), "Brasil");
        this.active = request.active() == null || request.active();
        this.updatedAt = OffsetDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = OffsetDateTime.now();
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public String getPersonType() { return personType; }
    public String getCpf() { return cpf; }
    public String getCnpj() { return cnpj; }
    public String getForeignId() { return foreignId; }
    public String getName() { return name; }
    public String getTradeName() { return tradeName; }
    public String getStateRegistrationIndicator() { return stateRegistrationIndicator; }
    public String getStateRegistration() { return stateRegistration; }
    public String getMunicipalRegistration() { return municipalRegistration; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getZipCode() { return zipCode; }
    public String getStreet() { return street; }
    public String getNumber() { return number; }
    public String getComplement() { return complement; }
    public String getDistrict() { return district; }
    public String getCityCodeIbge() { return cityCodeIbge; }
    public String getCityName() { return cityName; }
    public String getUf() { return uf; }
    public String getCountryCode() { return countryCode; }
    public String getCountryName() { return countryName; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
