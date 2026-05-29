package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SefazClient {
    public Map<String, Object> status(String uf, String environment) {
        return Map.of("uf", uf, "environment", environment, "status", "STUB", "message", "Consultar endpoints oficiais antes de transmissao real.");
    }

    public Map<String, Object> authorize(String signedXml) {
        return Map.of("code", "999", "reason", "Gateway SEFAZ em modo stub; transmissao real desabilitada.");
    }
}
