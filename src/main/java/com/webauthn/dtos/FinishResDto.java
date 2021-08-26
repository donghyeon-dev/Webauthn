package com.webauthn.dtos;

import com.yubico.webauthn.data.AuthenticatorData;
import com.yubico.webauthn.data.ByteArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;


/**
 * BlahBlah Entity 의 DT 를 도와주는 DTO 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinishResDto {

    final boolean success = true;
    RegResDto request;
    FinishReqDto response;
    CredentialRegistrationDto registration;
    boolean attestationTrusted;
    Optional<AttestationCertInfoDto> attestationCert;

    AuthenticatorData authData;

    String username;

    ByteArray sessionToken;
}
