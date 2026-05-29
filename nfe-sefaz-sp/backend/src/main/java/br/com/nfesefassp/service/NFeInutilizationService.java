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
public class NFeInutilizationService {
    public Map<String, Object> inutilize(UUID companyId, Map<String, Object> request) {
        return Map.of("companyId", companyId, "event", "INUTILIZACAO", "status", "STUB");
    }
}
