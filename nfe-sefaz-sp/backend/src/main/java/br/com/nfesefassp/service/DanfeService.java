package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DanfeService {
    public byte[] generate(NFe nfe) {
        String text = "DANFE MVP\nChave: " + nfe.getAccessKey() + "\nNumero: " + nfe.getNumber();
        return text.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generate(Map<String, Object> nfe) {
        String text = "DANFE MVP\nChave: " + nfe.get("access_key") + "\nNumero: " + nfe.get("number");
        return text.getBytes(StandardCharsets.UTF_8);
    }
}
