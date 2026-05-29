package com.example.nfe.common;

import java.time.OffsetDateTime;

public class AccessKeyService {
    public String generate(String ufCode, OffsetDateTime issueDate, String cnpj, String model, int series,
                           long number, String emissionType, int numericCode) {
        String base = ufCode
                + "%02d%02d".formatted(issueDate.getYear() % 100, issueDate.getMonthValue())
                + CnpjValidator.digits(cnpj)
                + model
                + "%03d".formatted(series)
                + "%09d".formatted(number)
                + emissionType
                + "%08d".formatted(numericCode);
        return base + checkDigit(base);
    }

    public int checkDigit(String base43) {
        int weight = 2;
        int sum = 0;
        for (int i = base43.length() - 1; i >= 0; i--) {
            sum += Character.digit(base43.charAt(i), 10) * weight;
            weight = weight == 9 ? 2 : weight + 1;
        }
        int mod = sum % 11;
        return mod == 0 || mod == 1 ? 0 : 11 - mod;
    }
}
