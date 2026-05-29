package br.com.nfesefassp.util;

public final class CpfValidator {
    private CpfValidator() {
    }

    public static boolean isValid(String value) {
        String cpf = value == null ? "" : value.replaceAll("\\D", "");
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        return digit(cpf, 9) == Character.digit(cpf.charAt(9), 10)
                && digit(cpf, 10) == Character.digit(cpf.charAt(10), 10);
    }

    private static int digit(String cpf, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += Character.digit(cpf.charAt(i), 10) * (length + 1 - i);
        }
        int result = 11 - (sum % 11);
        return result > 9 ? 0 : result;
    }
}
