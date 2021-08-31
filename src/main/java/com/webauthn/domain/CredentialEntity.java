package com.webauthn.domain;


import com.yubico.webauthn.data.ByteArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "credentials")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String credentialNickname;

    private LocalDateTime created;

    // RegisteredCredential 목록
    // ByteArray to Base64
    @Column
    private String credentialId;

    @Column String userHandle;

    @Column String publicKeyCose;

    private long signatureCount;


    private String getUserName(){
        return user.getName();
    }
}
