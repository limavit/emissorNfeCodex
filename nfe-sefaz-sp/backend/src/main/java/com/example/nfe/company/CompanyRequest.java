package com.example.nfe.company;

import jakarta.validation.constraints.NotBlank;

public record CompanyRequest(
        @NotBlank String cnpj,
        @NotBlank String corporateName,
        String tradeName,
        String stateRegistration,
        String municipalRegistration,
        @NotBlank String cnae,
        @NotBlank String taxRegime,
        @NotBlank String crt,
        @NotBlank String zipCode,
        @NotBlank String street,
        @NotBlank String number,
        String complement,
        @NotBlank String district,
        @NotBlank String cityCodeIbge,
        @NotBlank String cityName,
        @NotBlank String uf,
        String countryCode,
        String countryName,
        String phone,
        String email,
        String environment,
        Integer defaultSeries,
        Long nextNfeNumber,
        @NotBlank String defaultNatureOperation,
        String defaultPresenceIndicator,
        String defaultEmissionType,
        Boolean active
) {}
