package br.com.nfesefassp.util;

public final class CnpjValidator {
    private CnpjValidator() {
    }

    public static boolean isValid(String value) {
        String cnpj = digits(value);
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) {
            return false;
        }
        return digit(cnpj, 12) == Character.digit(cnpj.charAt(12), 10)
                && digit(cnpj, 13) == Character.digit(cnpj.charAt(13), 10);
    }

    public static String digits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private static int digit(String cnpj, int length) {
        int[] weights = length == 12
                ? new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
                : new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += Character.digit(cnpj.charAt(i), 10) * weights[i];
        }
        int mod = sum % 11;
        return mod < 2 ? 0 : 11 - mod;
    }
}
