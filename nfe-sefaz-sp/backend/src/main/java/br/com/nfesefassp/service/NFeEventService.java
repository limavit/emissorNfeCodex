package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

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
