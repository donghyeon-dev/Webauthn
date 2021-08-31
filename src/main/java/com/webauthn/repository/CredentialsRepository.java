package com.webauthn.repository;

import com.webauthn.domain.CredentialEntity;
import com.webauthn.domain.UserEntity;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCode.T;

@Repository
@RequiredArgsConstructor
public class CredentialsRepository implements CredentialRepository {

    private final UserRepository userRepository;
    private final com.webauthn.repository.CredentialRepository credentialEntityRepository;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        UserEntity targetEntity = userRepository.findUserEntityByName(username);
        if(ObjectUtils.isEmpty(targetEntity)){
            return new HashSet<>();
        } else {
            return
                targetEntity.getUserCredentials().stream()
                    .map(
                            credential ->
                                    PublicKeyCredentialDescriptor.builder()
                                            .id(ByteArray.fromBase64(credential.getCredentialId()))
                                            .build()
                    ).collect(Collectors.toSet());
        }
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
        List<CredentialEntity> list =credentialEntityRepository.findAll();
        if(ObjectUtils.isEmpty(list)){
            return new HashSet<>();
        } else {
        return credentialEntityRepository.findAll().stream()
                        .map(
                                reg ->
                                        RegisteredCredential.builder()
                                                .credentialId(ByteArray.fromBase64(reg.getCredentialId()))
                                                .userHandle(ByteArray.fromBase64(reg.getUserHandle()))
                                                .publicKeyCose(ByteArray.fromBase64(reg.getPublicKeyCose()))
                                                .signatureCount(reg.getSignatureCount())
                                                .build()
                        )
                        .collect(Collectors.toSet());
        }
    }

    public boolean userExist(String username){
        return !ObjectUtils.isEmpty(credentialEntityRepository.findCredentialEntityByUser_Name(username));
    }
}
