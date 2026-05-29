package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class NFeCalculationService {
    public Totals calculate(List<Item> items) {
        BigDecimal products = BigDecimal.ZERO;
        BigDecimal freight = BigDecimal.ZERO;
        BigDecimal insurance = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal other = BigDecimal.ZERO;
        BigDecimal icms = BigDecimal.ZERO;
        BigDecimal ipi = BigDecimal.ZERO;
        BigDecimal pis = BigDecimal.ZERO;
        BigDecimal cofins = BigDecimal.ZERO;

        for (Item item : items) {
            if (item.includeInTotal()) {
                products = products.add(item.grossTotal());
            }
            freight = freight.add(item.freight());
            insurance = insurance.add(item.insurance());
            discount = discount.add(item.discount());
            other = other.add(item.other());
            icms = icms.add(item.icms());
            ipi = ipi.add(item.ipi());
            pis = pis.add(item.pis());
            cofins = cofins.add(item.cofins());
        }
        BigDecimal invoice = products.add(freight).add(insurance).add(other).add(ipi).subtract(discount);
        return new Totals(money(products), money(freight), money(insurance), money(discount), money(other),
                money(icms), money(ipi), money(pis), money(cofins), money(invoice));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public record Item(BigDecimal grossTotal, BigDecimal freight, BigDecimal insurance, BigDecimal discount,
                       BigDecimal other, BigDecimal icms, BigDecimal ipi, BigDecimal pis, BigDecimal cofins,
                       boolean includeInTotal) {}
    public record Totals(BigDecimal products, BigDecimal freight, BigDecimal insurance, BigDecimal discount,
                         BigDecimal other, BigDecimal icms, BigDecimal ipi, BigDecimal pis, BigDecimal cofins,
                         BigDecimal invoice) {}
}
