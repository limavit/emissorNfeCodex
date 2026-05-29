package br.com.nfesefassp.model;

import java.util.List;
import java.util.UUID;

public record NFeRequest(
        UUID customerId,
        String natureOperation,
        String operationType,
        String destinationType,
        String purpose,
        String presenceIndicator,
        String emissionType,
        List<NFeItemRequest> items
) {}
