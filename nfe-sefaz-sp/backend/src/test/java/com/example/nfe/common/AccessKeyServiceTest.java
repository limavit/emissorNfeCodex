package com.example.nfe.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class AccessKeyServiceTest {
    @Test
    void generates44DigitAccessKey() {
        String key = new AccessKeyService().generate("35", OffsetDateTime.parse("2026-05-29T10:00:00-03:00"),
                "11222333000181", "55", 1, 1, "1", 12345678);

        assertThat(key).hasSize(44);
        assertThat(key).containsOnlyDigits();
    }
}
