package br.com.nfesefassp.model;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String internalCode,
        String ean,
        @NotBlank String description,
        @NotBlank String ncm,
        String cest,
        String cfopInternal,
        String cfopInterstate,
        String cfopExternal,
        String commercialUnit,
        String taxableUnit,
        BigDecimal conversionFactor,
        BigDecimal unitPrice,
        String origin,
        String itemType,
        BigDecimal grossWeight,
        BigDecimal netWeight,
        Boolean active
) {}
