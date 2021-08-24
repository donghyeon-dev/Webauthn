package com.webauthn.service;

import com.webauthn.domain.UserEntity;
import com.webauthn.dtos.*;
import com.webauthn.repository.CredentialsRepository;
import com.webauthn.repository.UserRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.*;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.omg.CORBA.DynAnyPackage.InvalidValue;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpServerErrorException;

import javax.management.InvalidAttributeValueException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final static SecureRandom random = new SecureRandom();

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


    public RegResDto startRegistration(RegReqDto requestBody) throws InvalidAttributeValueException {

        // 사용될 파라미터 선언
        String username = requestBody.getUsername();
        String displayName = requestBody.getDisplayName();
        String credentialNickname = requestBody.getCredentialNickname();
        boolean requiredResidentKey = requestBody.isRequireResidentKey();
        String sessionToken = requestBody.getSessionToken();
        ByteArray id = getRandomByte(32);

        //RelyingParty 선언
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("localhost")
                .name("Webauthn with Sprigboot")
                .build();

        RelyingParty rp = RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(credentialsRepository)
                .build();


        // 해당 username으로 된 Entity가 있는지 검색 후 없다면 새 Entity 생성
        UserEntity userOptional = userRepository.findUserEntityByName(username);

        if(ObjectUtils.isEmpty(userOptional)){

            // UserIdentity 생성
            UserIdentity regiUser = UserIdentity.builder()
                    .name(username)
                    .displayName(displayName)
                    .id(id)
                    .build();

            UserEntity newUser = UserEntity.builder()
                    .name(username)
                    .displayName(displayName)
                    .id(id.toString())
                    .build();

            RegResDto resDto = new RegResDto(
                    username,
                    Optional.ofNullable(credentialNickname),
                    id,
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
                    null);
            // 아마 이러고 내용을.. Redis나 캐시에 저장해야할듯?

            userRepository.save(newUser);
            userRepository.flush();
                    return resDto;
        } else {
            throw new InvalidAttributeValueException();
        }
    };
}
