package com.webauthn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.webauthn.dtos.entity.CredentialDto;
import com.webauthn.utils.CommonUtils;
import com.webauthn.domain.CredentialEntity;
import com.webauthn.domain.UserEntity;
import com.webauthn.dtos.registration.*;
import com.webauthn.repository.CredentialRepository;
import com.webauthn.repository.UserRepository;
import com.webauthn.utils.RedisUtils;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final CredentialRepository credentialEntityRepository;
    private final RedisUtils redisUtils;
    private final CommonUtils commonUtils;
    private final ModelMapper modelMapper;



    public RegResDto startRegistration(RegReqDto requestBody) throws InvalidAttributeValueException, JsonProcessingException {

        // 사용될 파라미터 선언
        String username = requestBody.getUsername();
        String displayName = requestBody.getDisplayName();
        String credentialNickname = requestBody.getCredentialNickname();
        ByteArray userId = commonUtils.getRandomByte(32);
        ByteArray requestId = commonUtils.getRandomByte(32);

        // 해당 username으로 된 Entity가 있는지 검색 후 없다면 새 Entity 생성
        UserEntity userOptional = userRepository.findUserEntityByName(username);

        if(ObjectUtils.isEmpty(userOptional)){

            // UserIdentity 생성
            UserIdentity regiUser = UserIdentity.builder()
                    .name(username)
                    .displayName(displayName)
                    .id(userId)
                    .build();

            UserEntity newUser = UserEntity.builder()
                    .name(username)
                    .displayName(displayName)
                    .id(userId.getBase64())
                    .build();

            // todo Builder패턴으로 변경해야함
            RegResDto resDto = new RegResDto(
                    username,
                    Optional.ofNullable(credentialNickname),
                    requestId,
                    commonUtils.rp().startRegistration(
                            StartRegistrationOptions.builder()
                                    .user(regiUser)
                                    .authenticatorSelection (
                                            AuthenticatorSelectionCriteria.builder()
                                                    .requireResidentKey(false)
                                                    .build()
                                    )
                            .build()
                            ),
                    userId);

            // 아마 이러고 RegRes값과 생성한 유저엔티티를. Redis에 보관
            RedisDto redisDto = RedisDto.builder()
                    .user(newUser)
                    .registrationResponse(resDto)
                    .build();
            redisUtils.putSession(requestId.getBase64(), redisDto);

            // userEntity 인서트
            userRepository.save(newUser);
            userRepository.flush();
                    return resDto;
        } else {
            throw new InvalidAttributeValueException("Username is already exist!!");
        }
    };

    public FinishResDto finishRegistration(FinishReqDto requestBody) throws JsonProcessingException, InvalidAttributeValueException, RegistrationFailedException {
        // 검증
        String requestId = requestBody.getRequestId().getBase64();
        boolean isInRedis = redisUtils.isRegisterRequestIdInRedis(requestId);
        if(!isInRedis){
            throw new InvalidAttributeValueException("Invalid RequestId!!");
        }
        RedisDto redisDto = redisUtils.getSession(requestId);
        RegResDto exRequest = redisDto.getRegistrationResponse();
        redisUtils.deleteSession(requestId);

        // Empty check
        if(ObjectUtils.isEmpty(exRequest)){
            throw new NullPointerException("Session is empty!!");
        }
        // session 내 저장된 값이 있다면
        RegistrationResult registrationResult =
                commonUtils.rp().finishRegistration(
                        FinishRegistrationOptions.builder()
                                .request(exRequest.getPublicKeyCredentialCreationOptions())
                                .response(requestBody.getCredential())
                                .build()
                );

        // 세션내 userName과 request userName 비교
        if(exRequest.getUsername().equals(requestBody.getUsername())){

            final boolean isValidSession = exRequest.getSessionToken().getBase64().equals(
                                    exRequest.getPublicKeyCredentialCreationOptions().getUser().getId().getBase64());
            if(!isValidSession){
                throw new InvalidAttributeValueException("Token is not valid!");
            }
            // SuccessResult 파라미터들 생성
            RegResDto regResDto = RegResDto.builder()
                    .username(exRequest.getUsername())
                    .credentialNickname(Optional.ofNullable(exRequest.getUsername()))
                    .requestId(exRequest.getRequestId())
                    .publicKeyCredentialCreationOptions(exRequest.getPublicKeyCredentialCreationOptions())
                    .sessionToken(exRequest.getSessionToken())
                    .build();
            // SuccessResult 생성

            // Credential 정보랑 UserEntity 인서트
            UserEntity targetUser = userRepository.findUserEntityByName(requestBody.getUsername());
            CredentialEntity credentialEntity = CredentialEntity.builder()
                    .credentialNickname(exRequest.getCredentialNickname().get())
                    .created(LocalDateTime.now())
                    .credentialId(registrationResult.getKeyId().getId().getBase64())
                    .userHandle(exRequest.getPublicKeyCredentialCreationOptions().getUser().getId().getBase64())
                    .publicKeyCose(registrationResult.getPublicKeyCose().getBase64())
                    .signatureCount(requestBody.getCredential().getResponse().getParsedAuthenticatorData().getSignatureCounter())
                    .user(targetUser)
                    .build();
            List<CredentialEntity> list = new ArrayList<>();
            list.add(credentialEntity);
            UserEntity updateUser = UserEntity.builder()
                    .userCredentials(list)
                    .userId(targetUser.getUserId())
                    .id(targetUser.getId())
                    .name(targetUser.getName())
                    .displayName(targetUser.getDisplayName())
                    .build();

            credentialEntityRepository.save(credentialEntity);
            userRepository.save(updateUser);
            credentialEntityRepository.flush();
            userRepository.flush();

            return FinishResDto.builder()
                    .request(regResDto)
                    .response(requestBody)
                    .registration(modelMapper.map(credentialEntity, CredentialDto.class))
                    .attestationTrusted(registrationResult.isAttestationTrusted())
                    .authData(requestBody.getCredential().getResponse().getParsedAuthenticatorData())
                    .username(regResDto.getUsername())
                    .sessionToken(exRequest.getPublicKeyCredentialCreationOptions().getUser().getId())
                    .attestationCert(Optional.ofNullable(
                                    requestBody
                                            .getCredential()
                                            .getResponse()
                                            .getAttestation()
                                            .getAttestationStatement()
                                            .get("x5c"))
                            .map(certs -> certs.get(0))
                            .flatMap(
                                    (JsonNode certDer) -> {
                                        try {
                                            return Optional.of(new ByteArray(certDer.binaryValue()));
                                        } catch (IOException e) {
                                            return Optional.empty();
                                        }
                                    })
                            .map(AttestationCertInfoDto::new))
                    .build();
        } else {
            throw new InvalidAttributeValueException("User is not exist!!");

        }



    }
}
