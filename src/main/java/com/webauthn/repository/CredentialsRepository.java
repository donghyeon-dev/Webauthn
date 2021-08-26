package com.webauthn.repository;

import com.webauthn.domain.CredentialEntity;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.Set;

@Repository
@AllArgsConstructor
public class CredentialsRepository implements CredentialRepository {

    private final UserRepository userRepository;
    private final com.webauthn.repository.CredentialRepository credentialEntityRepository;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        return null;
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return Optional.empty();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return null;
    }

    public boolean userExist(String username){
        return !ObjectUtils.isEmpty(credentialEntityRepository.findCredentialEntityByUser_Name(username));
    }
}
