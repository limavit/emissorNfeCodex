package com.example.nfe.nfe;

import org.springframework.stereotype.Service;

@Service
public class NFeSignatureService {
    public String sign(String xml) {
        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("XML obrigatorio para assinatura.");
        }
        return xml.replace("</infNFe>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo/></Signature></infNFe>");
    }
}
