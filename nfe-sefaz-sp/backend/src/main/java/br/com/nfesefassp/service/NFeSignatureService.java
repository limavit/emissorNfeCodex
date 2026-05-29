package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

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
