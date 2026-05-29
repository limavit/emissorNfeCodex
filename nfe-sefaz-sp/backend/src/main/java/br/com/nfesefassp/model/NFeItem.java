package br.com.nfesefassp.model;

import br.com.nfesefassp.service.NFeCalculationService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "nfe_items")
public class NFeItem {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "nfe_id")
    private UUID nfeId;
    @Column(name = "product_id")
    private UUID productId;
    @Column(name = "item_number")
    private Integer itemNumber;
    @Column(name = "product_code")
    private String productCode;
    private String description;
    private String ncm;
    private String cest;
    private String cfop;
    @Column(name = "commercial_unit")
    private String commercialUnit;
    @Column(name = "commercial_quantity")
    private BigDecimal commercialQuantity;
    @Column(name = "commercial_unit_value")
    private BigDecimal commercialUnitValue;
    @Column(name = "gross_total")
    private BigDecimal grossTotal;
    @Column(name = "taxable_unit")
    private String taxableUnit;
    @Column(name = "taxable_quantity")
    private BigDecimal taxableQuantity;
    @Column(name = "taxable_unit_value")
    private BigDecimal taxableUnitValue;
    @Column(name = "freight_value")
    private BigDecimal freightValue = BigDecimal.ZERO;
    @Column(name = "insurance_value")
    private BigDecimal insuranceValue = BigDecimal.ZERO;
    @Column(name = "discount_value")
    private BigDecimal discountValue = BigDecimal.ZERO;
    @Column(name = "other_expenses")
    private BigDecimal otherExpenses = BigDecimal.ZERO;
    @Column(name = "include_in_total")
    private boolean includeInTotal = true;
    @Column(name = "icms_origin")
    private String icmsOrigin;
    @Column(name = "icms_cst")
    private String icmsCst;
    @Column(name = "icms_csosn")
    private String icmsCsosn;
    @Column(name = "icms_base")
    private BigDecimal icmsBase = BigDecimal.ZERO;
    @Column(name = "icms_rate")
    private BigDecimal icmsRate;
    @Column(name = "icms_value")
    private BigDecimal icmsValue = BigDecimal.ZERO;
    @Column(name = "ipi_cst")
    private String ipiCst;
    @Column(name = "ipi_base")
    private BigDecimal ipiBase = BigDecimal.ZERO;
    @Column(name = "ipi_rate")
    private BigDecimal ipiRate;
    @Column(name = "ipi_value")
    private BigDecimal ipiValue = BigDecimal.ZERO;
    @Column(name = "pis_cst")
    private String pisCst;
    @Column(name = "pis_base")
    private BigDecimal pisBase = BigDecimal.ZERO;
    @Column(name = "pis_rate")
    private BigDecimal pisRate;
    @Column(name = "pis_value")
    private BigDecimal pisValue = BigDecimal.ZERO;
    @Column(name = "cofins_cst")
    private String cofinsCst;
    @Column(name = "cofins_base")
    private BigDecimal cofinsBase = BigDecimal.ZERO;
    @Column(name = "cofins_rate")
    private BigDecimal cofinsRate;
    @Column(name = "cofins_value")
    private BigDecimal cofinsValue = BigDecimal.ZERO;
    @Column(name = "additional_info")
    private String additionalInfo;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected NFeItem() {
    }

    public static NFeItem from(UUID nfeId, int itemNumber, NFeItemRequest request) {
        NFeItem item = new NFeItem();
        item.nfeId = nfeId;
        item.itemNumber = itemNumber;
        item.productId = request.productId();
        item.productCode = request.productCode();
        item.description = request.description();
        item.ncm = request.ncm();
        item.cest = request.cest();
        item.cfop = request.cfop();
        item.commercialUnit = request.commercialUnit();
        item.commercialQuantity = money4(request.commercialQuantity());
        item.commercialUnitValue = money10(request.commercialUnitValue());
        item.grossTotal = request.grossTotal() == null
                ? money(item.commercialQuantity.multiply(item.commercialUnitValue))
                : money(request.grossTotal());
        item.taxableUnit = valueOrDefault(request.taxableUnit(), item.commercialUnit);
        item.taxableQuantity = request.taxableQuantity() == null ? item.commercialQuantity : money4(request.taxableQuantity());
        item.taxableUnitValue = request.taxableUnitValue() == null ? item.commercialUnitValue : money10(request.taxableUnitValue());
        item.freightValue = money(request.freightValue());
        item.insuranceValue = money(request.insuranceValue());
        item.discountValue = money(request.discountValue());
        item.otherExpenses = money(request.otherExpenses());
        item.includeInTotal = request.includeInTotal() == null || request.includeInTotal();
        item.icmsOrigin = request.icmsOrigin();
        item.icmsCst = request.icmsCst();
        item.icmsCsosn = request.icmsCsosn();
        item.icmsBase = money(request.icmsBase());
        item.icmsRate = request.icmsRate();
        item.icmsValue = money(request.icmsValue());
        item.ipiCst = request.ipiCst();
        item.ipiBase = money(request.ipiBase());
        item.ipiRate = request.ipiRate();
        item.ipiValue = money(request.ipiValue());
        item.pisCst = request.pisCst();
        item.pisBase = money(request.pisBase());
        item.pisRate = request.pisRate();
        item.pisValue = money(request.pisValue());
        item.cofinsCst = request.cofinsCst();
        item.cofinsBase = money(request.cofinsBase());
        item.cofinsRate = request.cofinsRate();
        item.cofinsValue = money(request.cofinsValue());
        item.additionalInfo = request.additionalInfo();
        return item;
    }

    public NFeCalculationService.Item toCalculationItem() {
        return new NFeCalculationService.Item(grossTotal, freightValue, insuranceValue, discountValue,
                otherExpenses, icmsValue, ipiValue, pisValue, cofinsValue, includeInTotal);
    }

    private static BigDecimal money(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal money4(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(4, RoundingMode.HALF_UP);
    }

    private static BigDecimal money10(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(10, RoundingMode.HALF_UP);
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    public UUID getId() { return id; }
    public UUID getNfeId() { return nfeId; }
    public Integer getItemNumber() { return itemNumber; }
    public String getProductCode() { return productCode; }
    public String getDescription() { return description; }
    public String getNcm() { return ncm; }
    public String getCfop() { return cfop; }
    public BigDecimal getGrossTotal() { return grossTotal; }
    public BigDecimal getFreightValue() { return freightValue; }
    public BigDecimal getInsuranceValue() { return insuranceValue; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getOtherExpenses() { return otherExpenses; }
    public boolean isIncludeInTotal() { return includeInTotal; }
}
