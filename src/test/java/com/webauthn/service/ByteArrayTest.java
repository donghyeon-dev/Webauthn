package com.webauthn.service;

import com.yubico.webauthn.data.ByteArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ByteArrayTest {

    @Autowired
    RegistrationService registrationService;

    @Test
    public void ByteArray_스트링전환테스트(){
        ByteArray random = registrationService.getRandomByte(32);
        String randomToString = random.getBase64();
        ByteArray stringedRandomToBA = ByteArray.fromBase64(randomToString);

        assertEquals(random,stringedRandomToBA);
    }

    @Test
    public void Randon이_항상_같은지_테스트(){
        ByteArray random = registrationService.getRandomByte(32);
        ByteArray random2 = registrationService.getRandomByte(32);

        assertNotEquals(random,random2);
    }
}
