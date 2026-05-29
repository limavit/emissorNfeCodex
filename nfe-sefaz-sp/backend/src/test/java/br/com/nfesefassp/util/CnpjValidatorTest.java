package br.com.nfesefassp.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CnpjValidatorTest {
    @Test
    void validatesCnpj() {
        assertThat(CnpjValidator.isValid("11.222.333/0001-81")).isTrue();
        assertThat(CnpjValidator.isValid("11.111.111/1111-11")).isFalse();
    }
}
