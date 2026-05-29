package com.example.nfe.nfe;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DanfeService {
    public byte[] generate(Map<String, Object> nfe) {
        String text = "DANFE MVP\nChave: " + nfe.get("access_key") + "\nNumero: " + nfe.get("number");
        return text.getBytes(StandardCharsets.UTF_8);
    }
}
