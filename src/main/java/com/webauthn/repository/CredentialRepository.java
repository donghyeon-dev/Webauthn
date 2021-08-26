package com.webauthn.repository;

import com.webauthn.domain.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {

    CredentialEntity findCredentialEntityByUser_Name(String username);

}
