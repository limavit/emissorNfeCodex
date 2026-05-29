package com.example.nfe.sefaz;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NFeEventService {
    public Map<String, Object> cancel(UUID nfeId, String justification) {
        if (justification == null || justification.length() < 15) {
            throw new IllegalArgumentException("Justificativa deve ter pelo menos 15 caracteres.");
        }
        return Map.of("nfeId", nfeId, "event", "CANCELAMENTO", "status", "STUB");
    }

    public Map<String, Object> cce(UUID nfeId, String text) {
        return Map.of("nfeId", nfeId, "event", "CARTA_CORRECAO", "status", "STUB");
    }
}
