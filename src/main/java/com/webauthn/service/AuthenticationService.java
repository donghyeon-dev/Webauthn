package com.webauthn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webauthn.domain.CredentialEntity;
import com.webauthn.domain.UserEntity;
import com.webauthn.dtos.authentication.FinishAuthResDto;
import com.webauthn.dtos.entity.CredentialDto;
import com.webauthn.dtos.entity.UserDto;
import com.webauthn.dtos.registration.RedisDto;
import com.webauthn.repository.CredentialRepository;
import com.webauthn.repository.CredentialsRepository;
import com.webauthn.utils.CommonUtils;
import com.webauthn.dtos.authentication.FinishAuthReqDto;
import com.webauthn.dtos.authentication.StartAuthReqDto;
import com.webauthn.dtos.authentication.StartAuthResDto;
import com.webauthn.repository.UserRepository;
import com.webauthn.utils.RedisUtils;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.exception.AssertionFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.management.InvalidAttributeValueException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CommonUtils commonUtils;
    private final CredentialRepository credentialRepository;
    private final RedisUtils redisUtils;
    private final ModelMapper modelMapper;


    public StartAuthResDto startAuthenticate(StartAuthReqDto reqDto) throws JsonProcessingException {

        Optional<String> username = reqDto.getUsername();
        if(username.isPresent() && !userRepository.existsByName(username.get())){
            // username이 NULL이 아니고, username으로 조회되는 엔티티가 없다면,
            throw new IllegalArgumentException("user is not registred");
        } else {
            // AssertionRequest 생성
            AssertionRequest assertionRequest =
                    commonUtils.rp().startAssertion(
                            StartAssertionOptions.builder()
                                    .username(username)
                                    .build()
                    );
            // ResDto 생성
            StartAuthResDto resDto =  StartAuthResDto.builder()
                    .requestId(commonUtils.getRandomByte(32))
                    .publicKeyCredentialRequestOptions(assertionRequest.getPublicKeyCredentialRequestOptions())
                    .username(assertionRequest.getUsername())
                    .request(assertionRequest)
                    .build();

            //Redis session 저장
            RedisDto redisDto = RedisDto.builder()
                    .authenticateResponse(resDto)
                    .build();
            redisUtils.putSession(resDto.getRequestId().getBase64(), redisDto);

            return resDto;

        }
    }

    @Transactional
    public FinishAuthResDto finishAuthenticate(FinishAuthReqDto requestBody) throws Exception {

        String requestId = requestBody.getRequestId().getBase64();
        boolean isInRedis = redisUtils.isAuthenticateRequestIdInRedis(requestId);

        //검증
        if(!isInRedis){
            throw new InvalidAttributeValueException("Invalid RequestId!!");
        }
        RedisDto redisDto = redisUtils.getSession(requestId);
        StartAuthResDto exRequest = redisDto.getAuthenticateResponse();
        redisUtils.deleteSession(requestId);

        if(ObjectUtils.isEmpty(exRequest)){
            throw new NullPointerException("Session is empty!!");
        }

        // session 내 저장된 값이 있다면
        AssertionResult result =
                commonUtils.rp().finishAssertion(
                        FinishAssertionOptions.builder()
                                .request(exRequest.getRequest())
                                .response(requestBody.getCredential())
                                .build()
                );

        if(result.isSuccess()){
            UserEntity targetUser = userRepository.findUserEntityByName(exRequest.getUsername().get());

            // SignaturCount 업데이트
            credentialRepository.updateSignatureCount(
                    result.getSignatureCount(),
                    credentialRepository.findCredentialEntityByUser_UserId(targetUser.getUserId()).getId());

            credentialRepository.flush();
            CredentialDto credentialDto =
                modelMapper.map(
                    credentialRepository.findCredentialEntityByUser_UserId(targetUser.getUserId()),
                        CredentialDto.class
                );
            credentialDto.setUserId(targetUser.getUserId());

            // 세션토큰 생성
            String sessionId = commonUtils.getRandomByte(32).getBase64();
            redisDto.setSessionToken(sessionId);

            // 세션저장
            redisUtils.putSession(result.getUserHandle().getBase64(),
                    redisDto);
            //ResponseDto 생성
            FinishAuthResDto resDto = FinishAuthResDto.builder()
                    .request(exRequest)
                    .response(requestBody)
                    .registrations(credentialDto)
                    .username(result.getUsername())
                    .sessionToken(ByteArray.fromBase64(sessionId))
                    .warnings(result.getWarnings())
                    .build();

            return resDto;
        } else {
            throw new AssertionFailedException("AssertionFailed");
        }
    }
}
