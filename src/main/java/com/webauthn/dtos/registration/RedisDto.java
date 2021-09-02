package com.webauthn.dtos.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webauthn.domain.UserEntity;
import com.webauthn.dtos.authentication.StartAuthResDto;
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
public class RedisDto {

    // startRegistration에 put, finishRegistration에 get
    private RegResDto registrationResponse;

    private StartAuthResDto authenticateResponse;

    private UserEntity user;
}
