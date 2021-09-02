package com.webauthn.dtos.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
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
public class StartAuthResDto {

    private ByteArray requestId;

    private PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions;

    private Optional<String> username;

    private AssertionRequest request;
}
