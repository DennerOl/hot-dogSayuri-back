package com.erp.pdv.service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class QzSignatureService {
@Value("${qz.private.key.path}")
    private String privateKeyPath;

    public String sign(String payload) {
        try {
            PrivateKey privateKey = loadPrivateKey();

            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initSign(privateKey);
            signature.update(payload.getBytes(StandardCharsets.UTF_8));

            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao assinar payload QZ", e);
        }
    }

  private PrivateKey loadPrivateKey() {
    try {
        ClassPathResource resource =
                new ClassPathResource("qz/private-key.pem");

        try (PEMParser pemParser = new PEMParser(
                new InputStreamReader(resource.getInputStream()))) {

            Object object = pemParser.readObject();

            JcaPEMKeyConverter converter =
                    new JcaPEMKeyConverter();

            if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            }

            throw new IllegalArgumentException("Formato de chave não suportado");

        }
    } catch (Exception e) {
        throw new RuntimeException("Erro ao carregar chave privada QZ", e);
    }
}


}
//src\main\resources\qz\qz-private-key.pem
//
