package com.webauthn.utils;

import com.webauthn.repository.CredentialRepository;
import com.webauthn.repository.CredentialsRepository;
import com.webauthn.repository.UserRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;

/**
 * RandomByteArray Generator, RP Bean
 */
@Component
@Value
public class CommonUtils {

    private final CredentialsRepository credentialsRepository;
    private final static SecureRandom random = new SecureRandom();

    public final  RelyingPartyIdentity rpIdentity(){
        return RelyingPartyIdentity.builder()
                .id("localhost")
                .name("Webauthn with Sprigboot")
                .build();
    };


    public final  RelyingParty rp() {
    return  RelyingParty.builder()
                .identity(rpIdentity())
                .credentialRepository(credentialsRepository)
                .origins(new HashSet<String>(Collections.singleton("http://localhost:8088")))
                .build();
    }
    ;

    /**
     * Random Bytecode generator method
     * @param length
     * @return
     */
    public ByteArray getRandomByte(int length){
        byte[] bt = new byte[length];
        random.nextBytes(bt);

        return new ByteArray(bt);
    }
}
