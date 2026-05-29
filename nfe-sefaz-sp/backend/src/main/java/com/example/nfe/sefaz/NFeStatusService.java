package com.example.nfe.sefaz;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class NFeStatusService {
    private final SefazClient client;

    public NFeStatusService(SefazClient client) {
        this.client = client;
    }

    public Map<String, Object> status(String uf, String environment) {
        return client.status(uf, environment);
    }
}
