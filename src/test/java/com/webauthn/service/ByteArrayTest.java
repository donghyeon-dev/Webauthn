package com.webauthn.service;

import com.webauthn.utils.CommonUtils;
import com.yubico.webauthn.data.ByteArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ByteArrayTest {

    @Autowired
    CommonUtils commonUtils;

    @Test
    public void ByteArray_스트링전환테스트(){
        ByteArray random = commonUtils.getRandomByte(32);
        String randomToString = random.getBase64();
        ByteArray stringedRandomToBA = ByteArray.fromBase64(randomToString);

        assertEquals(random,stringedRandomToBA);
    }

    @Test
    public void Randon이_항상_같은지_테스트(){
        ByteArray random = commonUtils.getRandomByte(32);
        ByteArray random2 = commonUtils.getRandomByte(32);

        assertNotEquals(random,random2);
    }
}
