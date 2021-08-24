package com.webauthn.repository;

import com.webauthn.domain.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {

}
