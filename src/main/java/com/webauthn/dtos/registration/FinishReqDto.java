package com.webauthn.dtos.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
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
public class FinishReqDto {

    private String username;

    private Optional<String> credentialNickname;

    private  ByteArray requestId;

    private  PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>
            credential;

    private  Optional<ByteArray> sessionToken;
}
