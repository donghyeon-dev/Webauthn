package com.webauthn.dtos.registration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yubico.internal.util.CertificateParser;
import com.yubico.webauthn.data.ByteArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * BlahBlah Entity 의 DT 를 도와주는 DTO 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttestationCertInfoDto {

    private ByteArray der;

    private String text;

    public AttestationCertInfoDto(ByteArray certDer) {
        der = certDer;
        X509Certificate cert = null;
        try {
            cert = CertificateParser.parseDer(certDer.getBytes());
        } catch (CertificateException e) {
        }
        if (cert == null) {
            text = null;
        } else {
            text = cert.toString();
        }
    }
}
