package com.webauthn.domain;


import javassist.bytecode.ByteArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.net.URL;
import java.util.List;

@Entity(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    // yubico.ByteArray
    private String id;

    @Column(unique = true)
    private String name;

    private String displayName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(mappedBy = "id")
    private List<CredentialEntity> userCredentials;
}
