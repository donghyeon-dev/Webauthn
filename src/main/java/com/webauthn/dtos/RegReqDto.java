package com.webauthn.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * BlahBlah Entity 의 DT 를 도와주는 DTO 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegReqDto {

    private String username;

    private String displayName;

    private String credentialNickname;

    private boolean requireResidentKey;

    private String sessionToken;
}
