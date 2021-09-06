package com.webauthn.dtos.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webauthn.domain.CredentialEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


/**
 * BlahBlah Entity 의 DT 를 도와주는 DTO 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String id;

    private String name;

    private String displayName;

    private Long userId;

    private List<CredentialEntity> userCredentials;
}
