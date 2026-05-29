package br.com.nfesefassp.model;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductTaxRuleRequest(
        String ufOrigin,
        String ufDestination,
        String operationType,
        String taxRegime,
        @NotBlank String cfop,
        String icmsCst,
        String icmsCsosn,
        String icmsModBc,
        BigDecimal icmsRate,
        BigDecimal icmsBaseReduction,
        BigDecimal fcpRate,
        String icmsStModBc,
        BigDecimal icmsStMva,
        BigDecimal icmsStRate,
        BigDecimal icmsStBaseReduction,
        String ipiCst,
        BigDecimal ipiRate,
        String ipiEnquadramento,
        String pisCst,
        BigDecimal pisRate,
        String pisCalculationType,
        String cofinsCst,
        BigDecimal cofinsRate,
        String cofinsCalculationType,
        String benefitCode,
        LocalDate validFrom,
        LocalDate validUntil,
        Boolean active
) {}
