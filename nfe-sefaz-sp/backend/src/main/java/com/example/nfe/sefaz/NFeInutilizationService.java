package com.example.nfe.sefaz;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NFeInutilizationService {
    public Map<String, Object> inutilize(UUID companyId, Map<String, Object> request) {
        return Map.of("companyId", companyId, "event", "INUTILIZACAO", "status", "STUB");
    }
}
