package br.com.nfesefassp.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CpfValidatorTest {
    @Test
    void validatesCpf() {
        assertThat(CpfValidator.isValid("529.982.247-25")).isTrue();
        assertThat(CpfValidator.isValid("111.111.111-11")).isFalse();
    }
}
