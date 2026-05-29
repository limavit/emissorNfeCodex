package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

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
