package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class NFeXmlBuilderService {
    public String build(NFe nfe) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <NFe xmlns="http://www.portalfiscal.inf.br/nfe">
                  <infNFe Id="NFe%s" versao="4.00">
                    <ide>
                      <mod>55</mod>
                      <serie>%s</serie>
                      <nNF>%s</nNF>
                      <dhEmi>%s</dhEmi>
                      <natOp>%s</natOp>
                    </ide>
                  </infNFe>
                </NFe>
                """.formatted(nfe.getAccessKey(), nfe.getSeries(), nfe.getNumber(), nfe.getIssueDatetime(), nfe.getNatureOperation());
    }

    public String build(Map<String, Object> nfe) {
        String id = String.valueOf(nfe.getOrDefault("access_key", ""));
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <NFe xmlns="http://www.portalfiscal.inf.br/nfe">
                  <infNFe Id="NFe%s" versao="4.00">
                    <ide>
                      <mod>55</mod>
                      <serie>%s</serie>
                      <nNF>%s</nNF>
                      <dhEmi>%s</dhEmi>
                      <natOp>%s</natOp>
                    </ide>
                  </infNFe>
                </NFe>
                """.formatted(id, nfe.get("series"), nfe.get("number"), OffsetDateTime.now(), nfe.get("nature_operation"));
    }
}
