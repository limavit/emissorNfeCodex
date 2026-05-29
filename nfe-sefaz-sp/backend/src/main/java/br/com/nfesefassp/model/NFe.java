package br.com.nfesefassp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "nfe")
public class NFe {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "company_id")
    private UUID companyId;
    @Column(name = "customer_id")
    private UUID customerId;
    private String model = "55";
    private Integer series;
    private Long number;
    @Column(name = "access_key")
    private String accessKey;
    @Column(name = "nature_operation")
    private String natureOperation;
    @Column(name = "operation_type")
    private String operationType;
    @Column(name = "destination_type")
    private String destinationType;
    @Column(name = "issue_datetime")
    private OffsetDateTime issueDatetime = OffsetDateTime.now();
    @Column(name = "purpose")
    private String purpose;
    @Column(name = "presence_indicator")
    private String presenceIndicator;
    @Column(name = "emission_type")
    private String emissionType;
    private String status = NFeStatus.RASCUNHO.name();
    @Column(name = "sefaz_status_code")
    private String sefazStatusCode;
    @Column(name = "sefaz_status_reason")
    private String sefazStatusReason;
    @Column(name = "digest_value")
    private String digestValue;
    @Column(name = "total_products")
    private BigDecimal totalProducts = BigDecimal.ZERO;
    @Column(name = "total_icms")
    private BigDecimal totalIcms = BigDecimal.ZERO;
    @Column(name = "total_ipi")
    private BigDecimal totalIpi = BigDecimal.ZERO;
    @Column(name = "total_pis")
    private BigDecimal totalPis = BigDecimal.ZERO;
    @Column(name = "total_cofins")
    private BigDecimal totalCofins = BigDecimal.ZERO;
    @Column(name = "total_freight")
    private BigDecimal totalFreight = BigDecimal.ZERO;
    @Column(name = "total_insurance")
    private BigDecimal totalInsurance = BigDecimal.ZERO;
    @Column(name = "total_discount")
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    @Column(name = "total_other_expenses")
    private BigDecimal totalOtherExpenses = BigDecimal.ZERO;
    @Column(name = "total_invoice")
    private BigDecimal totalInvoice = BigDecimal.ZERO;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected NFe() {
    }

    public static NFe draft(UUID companyId, NFeRequest request, Company company, String accessKey) {
        NFe nfe = new NFe();
        nfe.companyId = companyId;
        nfe.customerId = request.customerId();
        nfe.series = company.getDefaultSeries();
        nfe.number = company.getNextNfeNumber();
        nfe.accessKey = accessKey;
        nfe.natureOperation = valueOrDefault(request.natureOperation(), company.getDefaultNatureOperation());
        nfe.operationType = valueOrDefault(request.operationType(), "SAIDA");
        nfe.destinationType = valueOrDefault(request.destinationType(), "INTERNA");
        nfe.purpose = valueOrDefault(request.purpose(), "NORMAL");
        nfe.presenceIndicator = valueOrDefault(request.presenceIndicator(), "9");
        nfe.emissionType = valueOrDefault(request.emissionType(), "1");
        return nfe;
    }

    public void markStatus(NFeStatus status) {
        this.status = status.name();
        this.updatedAt = OffsetDateTime.now();
    }

    public void markSefazResult(NFeStatus status, Object code, Object reason) {
        this.status = status.name();
        this.sefazStatusCode = code == null ? null : String.valueOf(code);
        this.sefazStatusReason = reason == null ? null : String.valueOf(reason);
        this.updatedAt = OffsetDateTime.now();
    }

    public void markSigned(String digestValue) {
        this.status = NFeStatus.ASSINADA.name();
        this.digestValue = digestValue;
        this.updatedAt = OffsetDateTime.now();
    }

    public void applyTotals(br.com.nfesefassp.service.NFeCalculationService.Totals totals) {
        this.totalProducts = totals.products();
        this.totalFreight = totals.freight();
        this.totalInsurance = totals.insurance();
        this.totalDiscount = totals.discount();
        this.totalOtherExpenses = totals.other();
        this.totalIcms = totals.icms();
        this.totalIpi = totals.ipi();
        this.totalPis = totals.pis();
        this.totalCofins = totals.cofins();
        this.totalInvoice = totals.invoice();
        this.updatedAt = OffsetDateTime.now();
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public UUID getCustomerId() { return customerId; }
    public String getModel() { return model; }
    public Integer getSeries() { return series; }
    public Long getNumber() { return number; }
    public String getAccessKey() { return accessKey; }
    public String getNatureOperation() { return natureOperation; }
    public String getOperationType() { return operationType; }
    public String getDestinationType() { return destinationType; }
    public OffsetDateTime getIssueDatetime() { return issueDatetime; }
    public String getPurpose() { return purpose; }
    public String getPresenceIndicator() { return presenceIndicator; }
    public String getEmissionType() { return emissionType; }
    public String getStatus() { return status; }
    public String getSefazStatusCode() { return sefazStatusCode; }
    public String getSefazStatusReason() { return sefazStatusReason; }
    public String getDigestValue() { return digestValue; }
    public BigDecimal getTotalProducts() { return totalProducts; }
    public BigDecimal getTotalIcms() { return totalIcms; }
    public BigDecimal getTotalIpi() { return totalIpi; }
    public BigDecimal getTotalPis() { return totalPis; }
    public BigDecimal getTotalCofins() { return totalCofins; }
    public BigDecimal getTotalFreight() { return totalFreight; }
    public BigDecimal getTotalInsurance() { return totalInsurance; }
    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public BigDecimal getTotalOtherExpenses() { return totalOtherExpenses; }
    public BigDecimal getTotalInvoice() { return totalInvoice; }
}
