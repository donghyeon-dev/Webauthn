package com.webauthn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.webauthn.domain.UserEntity;
import com.webauthn.dtos.*;
import com.webauthn.repository.CredentialRepository;
import com.webauthn.repository.CredentialsRepository;
import com.webauthn.repository.UserRepository;
import com.webauthn.utils.RedisUtils;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final CredentialRepository credentialEntityRepository;
    private final static SecureRandom random = new SecureRandom();
    private final RedisUtils redisUtils;

    /**
     * id값 생성을 위한 RandomBytecode 생성 메서드
     * @param length
     * @return
     */
    public ByteArray getRandomByte(int length){
        byte[] bt = new byte[length];
        random.nextBytes(bt);

        return new ByteArray(bt);
    }

    public RegResDto startRegistration(RegReqDto requestBody) throws InvalidAttributeValueException, JsonProcessingException {

        // 사용될 파라미터 선언
        String username = requestBody.getUsername();
        String displayName = requestBody.getDisplayName();
        String credentialNickname = requestBody.getCredentialNickname();
        boolean requiredResidentKey = requestBody.isRequireResidentKey();
        ByteArray userId = getRandomByte(32);
        ByteArray requestId = getRandomByte(32);

        //RelyingParty 선언
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("localhost")
                .name("Webauthn with Sprigboot")
                .build();

        RelyingParty rp = RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(credentialsRepository)
                .origins(new HashSet<String>(Collections.singleton("http://localhost:8088")))
                .build();

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
                    rp.startRegistration(
                            StartRegistrationOptions.builder()
                                    .user(regiUser)
                                    .authenticatorSelection (
                                            AuthenticatorSelectionCriteria.builder()
                                                    .requireResidentKey(requiredResidentKey)
                                                    .build()
                                    )
                            .build()
                            ),
                    userId);

            // 아마 이러고 RegRes값과 생성한 유저엔티티를. Redis에 보관
            RedisDto redisDto = RedisDto.builder()
                    .credentialNickname(Optional.ofNullable(credentialNickname))
                    .publicKeyCredentialCreationOptions(resDto.getPublicKeyCredentialCreationOptions())
                    .requestId(requestId)
                    .sessionToken(userId)
                    .user(newUser)
                    .username(username)
                    .build();
            redisUtils.putSession(requestId.getBase64(), redisDto);

//            userRepository.save(newUser);
//            userRepository.flush();
                    return resDto;
        } else {
            throw new InvalidAttributeValueException();
        }
    };

    public FinishResDto finishRegistration(FinishReqDto requestBody) throws JsonProcessingException, InvalidAttributeValueException, RegistrationFailedException {

        //RelyingParty 선언
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("localhost")
                .name("Webauthn with Sprigboot")
                .build();

        RelyingParty rp = RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(credentialsRepository)
                .origins(new HashSet<String>(Collections.singleton("http://localhost:8088")))
                .build();


        // 검증
        String requestId = requestBody.getRequestId().getBase64();
        boolean isInRedis = redisUtils.isRequestIdInRedis(requestId);
        if(!isInRedis){
            throw new InvalidAttributeValueException("Username is already in session!!");
        }
        RedisDto exRequest = redisUtils.getSession(requestId);
        redisUtils.deleteSession(requestId);

        // Empty check
        if(ObjectUtils.isEmpty(exRequest)){
            throw new InvalidAttributeValueException("Session is empty!!");
        }
        // session 내 저장된 값이 있다면
        RegistrationResult registrationResult =
                rp.finishRegistration(
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
            FinishResDto finishResDto = FinishResDto.builder()
                    .request(regResDto)
                    .response(requestBody)
                    .registration(CredentialRegistrationDto.builder()
                            .userIdentity(exRequest.getPublicKeyCredentialCreationOptions().getUser())
                            .name(exRequest.getCredentialNickname())
                            .response(requestBody)
                            .result(registrationResult)
                            .build())
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

            // Credential 정보랑 UserEntity 업데이트


            return finishResDto;
        } else {
            throw new InvalidAttributeValueException("User is not exist!!");

        }



    }
}
