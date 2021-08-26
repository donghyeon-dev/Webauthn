package com.webauthn.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.UserIdentity;
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
public class CredentialRegistrationDto {

    private UserIdentity userIdentity;

    private Optional<String> name;

    private FinishReqDto response;

    private RegistrationResult result;
}
