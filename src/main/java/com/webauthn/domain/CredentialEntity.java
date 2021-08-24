package com.webauthn.domain;


import com.yubico.webauthn.data.ByteArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column
    private String credentialId;

    private int coseAlgorithm;

    @Basic(fetch = FetchType.LAZY)
    private byte[] publicKey;
}
