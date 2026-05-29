package com.example.nfe.sefaz;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class NFeTransmissionService {
    private final SefazClient client;

    public NFeTransmissionService(SefazClient client) {
        this.client = client;
    }

    public Map<String, Object> transmit(String signedXml) {
        return client.authorize(signedXml);
    }
}
