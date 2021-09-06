package com.webauthn.dtos.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webauthn.domain.CredentialEntity;
import com.webauthn.dtos.entity.CredentialDto;
import com.yubico.webauthn.data.ByteArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;


/**
 * BlahBlah Entity 의 DT 를 도와주는 DTO 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinishAuthResDto {

    private StartAuthResDto request;

    private FinishAuthReqDto response;

    private CredentialDto registrations;

    private String username;

    private ByteArray sessionToken;

    private List<String> warnings;

}
