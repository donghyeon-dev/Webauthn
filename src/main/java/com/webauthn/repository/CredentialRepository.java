package com.webauthn.repository;

import com.webauthn.domain.CredentialEntity;
import com.webauthn.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {

    CredentialEntity findCredentialEntityByUser_Name(String username);

    CredentialEntity findCredentialEntityByUser_UserId(long id);

    CredentialEntity findCredentialEntityByUserHandle(String userhandle);

    CredentialEntity findCredentialEntityByCredentialId(String credentialId);

    CredentialEntity findCredentialEntitiesByCredentialIdAndUser_Name(String credentialId, String username);

    @Modifying
    @Query("UPDATE credentials c SET c.signatureCount = :count WHERE c.id = :id")
    void updateSignatureCount(@Param("count") long count, @Param("id") long id);
}
