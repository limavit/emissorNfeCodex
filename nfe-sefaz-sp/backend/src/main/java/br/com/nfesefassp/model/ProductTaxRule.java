package br.com.nfesefassp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_tax_rules")
public class ProductTaxRule {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "company_id")
    private UUID companyId;
    @Column(name = "product_id")
    private UUID productId;
    @Column(name = "uf_origin")
    private String ufOrigin;
    @Column(name = "uf_destination")
    private String ufDestination;
    @Column(name = "operation_type")
    private String operationType;
    @Column(name = "tax_regime")
    private String taxRegime;
    private String cfop;
    @Column(name = "icms_cst")
    private String icmsCst;
    @Column(name = "icms_csosn")
    private String icmsCsosn;
    @Column(name = "icms_mod_bc")
    private String icmsModBc;
    @Column(name = "icms_rate")
    private BigDecimal icmsRate;
    @Column(name = "icms_base_reduction")
    private BigDecimal icmsBaseReduction;
    @Column(name = "fcp_rate")
    private BigDecimal fcpRate;
    @Column(name = "icms_st_mod_bc")
    private String icmsStModBc;
    @Column(name = "icms_st_mva")
    private BigDecimal icmsStMva;
    @Column(name = "icms_st_rate")
    private BigDecimal icmsStRate;
    @Column(name = "icms_st_base_reduction")
    private BigDecimal icmsStBaseReduction;
    @Column(name = "ipi_cst")
    private String ipiCst;
    @Column(name = "ipi_rate")
    private BigDecimal ipiRate;
    @Column(name = "ipi_enquadramento")
    private String ipiEnquadramento;
    @Column(name = "pis_cst")
    private String pisCst;
    @Column(name = "pis_rate")
    private BigDecimal pisRate;
    @Column(name = "pis_calculation_type")
    private String pisCalculationType;
    @Column(name = "cofins_cst")
    private String cofinsCst;
    @Column(name = "cofins_rate")
    private BigDecimal cofinsRate;
    @Column(name = "cofins_calculation_type")
    private String cofinsCalculationType;
    @Column(name = "benefit_code")
    private String benefitCode;
    @Column(name = "valid_from")
    private LocalDate validFrom = LocalDate.now();
    @Column(name = "valid_until")
    private LocalDate validUntil;
    private boolean active = true;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected ProductTaxRule() {
    }

    public static ProductTaxRule from(UUID companyId, UUID productId, ProductTaxRuleRequest request) {
        ProductTaxRule rule = new ProductTaxRule();
        rule.companyId = companyId;
        rule.productId = productId;
        rule.apply(request);
        return rule;
    }

    public void apply(ProductTaxRuleRequest request) {
        this.ufOrigin = valueOrDefault(request.ufOrigin(), "SP");
        this.ufDestination = valueOrDefault(request.ufDestination(), "SP");
        this.operationType = valueOrDefault(request.operationType(), "VENDA");
        this.taxRegime = valueOrDefault(request.taxRegime(), "REGIME_NORMAL");
        this.cfop = request.cfop();
        this.icmsCst = request.icmsCst();
        this.icmsCsosn = request.icmsCsosn();
        this.icmsModBc = request.icmsModBc();
        this.icmsRate = request.icmsRate();
        this.icmsBaseReduction = request.icmsBaseReduction();
        this.fcpRate = request.fcpRate();
        this.icmsStModBc = request.icmsStModBc();
        this.icmsStMva = request.icmsStMva();
        this.icmsStRate = request.icmsStRate();
        this.icmsStBaseReduction = request.icmsStBaseReduction();
        this.ipiCst = request.ipiCst();
        this.ipiRate = request.ipiRate();
        this.ipiEnquadramento = request.ipiEnquadramento();
        this.pisCst = request.pisCst();
        this.pisRate = request.pisRate();
        this.pisCalculationType = request.pisCalculationType();
        this.cofinsCst = request.cofinsCst();
        this.cofinsRate = request.cofinsRate();
        this.cofinsCalculationType = request.cofinsCalculationType();
        this.benefitCode = request.benefitCode();
        this.validFrom = request.validFrom() == null ? LocalDate.now() : request.validFrom();
        this.validUntil = request.validUntil();
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
    public UUID getProductId() { return productId; }
    public String getUfOrigin() { return ufOrigin; }
    public String getUfDestination() { return ufDestination; }
    public String getOperationType() { return operationType; }
    public String getTaxRegime() { return taxRegime; }
    public String getCfop() { return cfop; }
    public String getIcmsCst() { return icmsCst; }
    public String getIcmsCsosn() { return icmsCsosn; }
    public BigDecimal getIcmsRate() { return icmsRate; }
    public String getPisCst() { return pisCst; }
    public BigDecimal getPisRate() { return pisRate; }
    public String getCofinsCst() { return cofinsCst; }
    public BigDecimal getCofinsRate() { return cofinsRate; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
    public boolean isActive() { return active; }
}
