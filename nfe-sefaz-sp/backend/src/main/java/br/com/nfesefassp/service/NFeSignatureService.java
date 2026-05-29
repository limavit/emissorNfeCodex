package br.com.nfesefassp.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;
import javax.xml.XMLConstants;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service
public class NFeSignatureService {
    private static final String NFE_NAMESPACE = "http://www.portalfiscal.inf.br/nfe";
    private static final String XMLDSIG_NAMESPACE = XMLSignature.XMLNS;

    private final CertificateService certificateService;

    public NFeSignatureService(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    public SignedXml sign(UUID companyId, String xml) {
        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("XML obrigatorio para assinatura.");
        }

        try {
            Document document = parse(xml);
            Element infNFe = infNFe(document);
            String id = infNFe.getAttribute("Id");
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("Elemento infNFe sem atributo Id.");
            }
            infNFe.setIdAttribute("Id", true);

            CertificateService.SigningCertificate signingCertificate = certificateService.loadSigningCertificate(companyId);
            XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
            Reference reference = factory.newReference(
                    "#" + id,
                    factory.newDigestMethod(DigestMethod.SHA1, null),
                    List.of(
                            factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null),
                            factory.newTransform(CanonicalizationMethod.INCLUSIVE, (TransformParameterSpec) null)),
                    null,
                    null);
            SignedInfo signedInfo = factory.newSignedInfo(
                    factory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    List.of(reference));
            KeyInfo keyInfo = keyInfo(factory, signingCertificate.certificate());

            DOMSignContext context = new DOMSignContext(signingCertificate.privateKey(), infNFe.getParentNode());
            context.setDefaultNamespacePrefix("");
            XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);
            signature.sign(context);

            String signedXml = serialize(document);
            if (!validateSignature(signedXml, signingCertificate.certificate())) {
                throw new IllegalStateException("Assinatura XMLDSig gerada, mas a validacao local falhou.");
            }
            return new SignedXml(signedXml, digestValue(document));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao assinar XML da NF-e com certificado A1.", e);
        }
    }

    public boolean validateSignature(String signedXml, X509Certificate certificate) {
        try {
            Document document = parse(signedXml);
            Element infNFe = infNFe(document);
            infNFe.setIdAttribute("Id", true);
            NodeList signatures = document.getElementsByTagNameNS(XMLDSIG_NAMESPACE, "Signature");
            if (signatures.getLength() != 1) {
                return false;
            }
            DOMValidateContext context = new DOMValidateContext(certificate.getPublicKey(), signatures.item(0));
            return XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(context).validate(context);
        } catch (Exception e) {
            return false;
        }
    }

    private KeyInfo keyInfo(XMLSignatureFactory factory, X509Certificate certificate) {
        KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
        X509Data x509Data = keyInfoFactory.newX509Data(List.of(certificate));
        return keyInfoFactory.newKeyInfo(List.of(x509Data));
    }

    private Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private Element infNFe(Document document) {
        NodeList elements = document.getElementsByTagNameNS(NFE_NAMESPACE, "infNFe");
        if (elements.getLength() != 1) {
            throw new IllegalArgumentException("XML deve conter exatamente um elemento infNFe.");
        }
        return (Element) elements.item(0);
    }

    private String digestValue(Document document) {
        NodeList digests = document.getElementsByTagNameNS(XMLDSIG_NAMESPACE, "DigestValue");
        if (digests.getLength() == 0) {
            return null;
        }
        return digests.item(0).getTextContent();
    }

    private String serialize(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        var transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    public record SignedXml(String xml, String digestValue) {
    }
}
