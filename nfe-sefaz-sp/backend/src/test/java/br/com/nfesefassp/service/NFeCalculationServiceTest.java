package br.com.nfesefassp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class NFeCalculationServiceTest {
    @Test
    void calculatesTotalsWithBigDecimal() {
        NFeCalculationService service = new NFeCalculationService();
        var totals = service.calculate(List.of(
                new NFeCalculationService.Item(new BigDecimal("100.00"), new BigDecimal("10.00"),
                        BigDecimal.ZERO, new BigDecimal("5.00"), new BigDecimal("2.00"),
                        new BigDecimal("18.00"), BigDecimal.ZERO, new BigDecimal("1.65"),
                        new BigDecimal("7.60"), true)));

        assertThat(totals.products()).isEqualByComparingTo("100.00");
        assertThat(totals.invoice()).isEqualByComparingTo("107.00");
    }
}
