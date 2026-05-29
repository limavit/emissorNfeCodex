package br.com.nfesefassp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record NFeItemRequest(
        UUID productId,
        @NotBlank String productCode,
        @NotBlank String description,
        @NotBlank String ncm,
        String cest,
        @NotBlank String cfop,
        @NotBlank String commercialUnit,
        @Positive BigDecimal commercialQuantity,
        @Positive BigDecimal commercialUnitValue,
        BigDecimal grossTotal,
        String taxableUnit,
        BigDecimal taxableQuantity,
        BigDecimal taxableUnitValue,
        BigDecimal freightValue,
        BigDecimal insuranceValue,
        BigDecimal discountValue,
        BigDecimal otherExpenses,
        Boolean includeInTotal,
        String icmsOrigin,
        String icmsCst,
        String icmsCsosn,
        BigDecimal icmsBase,
        BigDecimal icmsRate,
        BigDecimal icmsValue,
        String ipiCst,
        BigDecimal ipiBase,
        BigDecimal ipiRate,
        BigDecimal ipiValue,
        String pisCst,
        BigDecimal pisBase,
        BigDecimal pisRate,
        BigDecimal pisValue,
        String cofinsCst,
        BigDecimal cofinsBase,
        BigDecimal cofinsRate,
        BigDecimal cofinsValue,
        String additionalInfo
) {}
