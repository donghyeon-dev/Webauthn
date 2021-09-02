package com.webauthn.repository;

import com.webauthn.domain.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {

    CredentialEntity findCredentialEntityByUser_Name(String username);

    List<CredentialEntity> findAll();

    CredentialEntity findCredentialEntitiesByCredentialIdAndUser_Name(String credentialId, String username);

    @Query("UPDATE credentials c SET c.signatureCount = :count WHERE c.credentialId = :credentialId")
    boolean updateSignatureCount(long count,String credentialId);
}
