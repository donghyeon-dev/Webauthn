package com.webauthn.dtos.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
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
public class RegResDto {

    private String username;

    private Optional<String> credentialNickname;

    private ByteArray requestId;

    private PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions;

    private ByteArray sessionToken;

}
