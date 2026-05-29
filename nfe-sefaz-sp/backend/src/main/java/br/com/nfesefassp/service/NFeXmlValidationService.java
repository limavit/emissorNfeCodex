package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

import org.springframework.stereotype.Service;

@Service
public class NFeXmlValidationService {
    public void validate(String xml) {
        if (xml == null || !xml.contains("<NFe")) {
            throw new IllegalArgumentException("XML da NF-e invalido.");
        }
        // XSD real deve ser carregado de resources/schemas/nfe/v4.00 apos atualizar schemas oficiais.
    }
}
