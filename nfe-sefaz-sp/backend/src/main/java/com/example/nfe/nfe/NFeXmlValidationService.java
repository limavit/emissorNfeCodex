package com.example.nfe.nfe;

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
