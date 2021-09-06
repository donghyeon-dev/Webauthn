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
    // 완성해야함
    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        String byteArray = credentialEntityRepository.findCredentialEntityByUser_Name(username).getUser().getId();
        return Optional.of(ByteArray.fromBase64(byteArray));
    }
    // 완성해야함
    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        CredentialEntity targetCredential= credentialEntityRepository.findCredentialEntityByUserHandle(userHandle.getBase64());
        return Optional.of(targetCredential.getUser().getName());
    }
    // 완성해야
    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        CredentialEntity targetEntity = credentialEntityRepository.findCredentialEntityByCredentialId(credentialId.getBase64());
        RegisteredCredential result = RegisteredCredential.builder()
                .credentialId(ByteArray.fromBase64(targetEntity.getCredentialId()))
                .userHandle(ByteArray.fromBase64(targetEntity.getUserHandle()))
                .publicKeyCose(ByteArray.fromBase64(targetEntity.getPublicKeyCose()))
                .signatureCount(targetEntity.getSignatureCount())
                .build();

        return Optional.of(result);
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
