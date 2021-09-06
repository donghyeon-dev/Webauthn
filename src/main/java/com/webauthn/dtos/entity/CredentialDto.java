package com.webauthn.dtos.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webauthn.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


/**
 * BlahBlah Entity 의 DT 를 도와주는 DTO 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CredentialDto {

    private Long id;

    private long userId;

    private String credentialNickname;

    private LocalDateTime created;

    private String credentialId;

    private String userHandle;

    private String publicKeyCose;

    private long signatureCount;
}
