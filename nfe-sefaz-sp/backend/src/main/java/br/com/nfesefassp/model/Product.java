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
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "company_id")
    private UUID companyId;
    @Column(name = "internal_code")
    private String internalCode;
    private String ean;
    private String description;
    private String ncm;
    private String cest;
    @Column(name = "cfop_internal")
    private String cfopInternal;
    @Column(name = "cfop_interstate")
    private String cfopInterstate;
    @Column(name = "cfop_external")
    private String cfopExternal;
    @Column(name = "commercial_unit")
    private String commercialUnit;
    @Column(name = "taxable_unit")
    private String taxableUnit;
    @Column(name = "conversion_factor")
    private BigDecimal conversionFactor = BigDecimal.ONE;
    @Column(name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private String origin = "0";
    @Column(name = "item_type")
    private String itemType;
    @Column(name = "gross_weight")
    private BigDecimal grossWeight;
    @Column(name = "net_weight")
    private BigDecimal netWeight;
    private boolean active = true;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected Product() {
    }

    public static Product from(UUID companyId, ProductRequest request) {
        Product product = new Product();
        product.companyId = companyId;
        product.apply(request);
        return product;
    }

    public void apply(ProductRequest request) {
        this.internalCode = request.internalCode();
        this.ean = request.ean();
        this.description = request.description();
        this.ncm = request.ncm();
        this.cest = request.cest();
        this.cfopInternal = request.cfopInternal();
        this.cfopInterstate = request.cfopInterstate();
        this.cfopExternal = request.cfopExternal();
        this.commercialUnit = valueOrDefault(request.commercialUnit(), "UN");
        this.taxableUnit = valueOrDefault(request.taxableUnit(), "UN");
        this.conversionFactor = request.conversionFactor() == null ? BigDecimal.ONE : request.conversionFactor();
        this.unitPrice = request.unitPrice() == null ? BigDecimal.ZERO : request.unitPrice();
        this.origin = valueOrDefault(request.origin(), "0");
        this.itemType = valueOrDefault(request.itemType(), "MERCADORIA_REVENDA");
        this.grossWeight = request.grossWeight();
        this.netWeight = request.netWeight();
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
    public String getInternalCode() { return internalCode; }
    public String getEan() { return ean; }
    public String getDescription() { return description; }
    public String getNcm() { return ncm; }
    public String getCest() { return cest; }
    public String getCfopInternal() { return cfopInternal; }
    public String getCfopInterstate() { return cfopInterstate; }
    public String getCfopExternal() { return cfopExternal; }
    public String getCommercialUnit() { return commercialUnit; }
    public String getTaxableUnit() { return taxableUnit; }
    public BigDecimal getConversionFactor() { return conversionFactor; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getOrigin() { return origin; }
    public String getItemType() { return itemType; }
    public BigDecimal getGrossWeight() { return grossWeight; }
    public BigDecimal getNetWeight() { return netWeight; }
    public boolean isActive() { return active; }
}
