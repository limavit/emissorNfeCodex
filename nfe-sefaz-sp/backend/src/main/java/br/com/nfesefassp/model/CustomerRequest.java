package br.com.nfesefassp.model;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        String personType,
        String cpf,
        String cnpj,
        String foreignId,
        @NotBlank String name,
        String tradeName,
        String stateRegistrationIndicator,
        String stateRegistration,
        String municipalRegistration,
        String email,
        String phone,
        String zipCode,
        String street,
        String number,
        String complement,
        String district,
        String cityCodeIbge,
        String cityName,
        String uf,
        String countryCode,
        String countryName,
        Boolean active
) {}
