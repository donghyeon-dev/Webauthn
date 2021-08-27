package com.webauthn.domain;


import com.yubico.webauthn.data.ByteArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity(name = "credentials")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long signatureCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String credentialNickname;


    // RegisteredCredential 목록
    // ByteArray to Base64
    @Column
    private String credentialId;

    @Column String userHandle;

    @Column String publicKeyCose;





    private LocalDateTime created;

    private String getUserName(){
        return user.getName();
    }
}
