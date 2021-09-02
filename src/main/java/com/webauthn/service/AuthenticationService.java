package com.webauthn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webauthn.domain.CredentialEntity;
import com.webauthn.dtos.registration.RedisDto;
import com.webauthn.repository.CredentialRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.management.InvalidAttributeValueException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CommonUtils commonUtils;
    private final RegistrationService registrationService;
    private final CredentialRepository credentialRepository;
    private final RedisUtils redisUtils;


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

    public String finishAuthenticate(FinishAuthReqDto requestBody) throws Exception {

        String requestId = requestBody.getRequestId().getBase64();
        boolean isInRedis = redisUtils.isAuthenticateRequestIdInRedis(requestId);

        //검증
        if(!isInRedis){
            throw new InvalidAttributeValueException("Invalid RequestId!!");
        }
        StartAuthResDto exRequest = redisUtils.getSession(requestId).getAuthenticateResponse();
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

//        if(result.isSuccess()){용
//            credentialRepository.updateSignatureCount()
//        }

        return "Hi";
    }
}
