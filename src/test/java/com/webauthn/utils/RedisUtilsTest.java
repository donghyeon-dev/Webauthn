package com.webauthn.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webauthn.dtos.RedisDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class RedisUtilsTest {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Test
    public void deleteSession_테스트() throws JsonProcessingException {
        RedisDto redisDto = RedisDto.builder().build();
        redisUtils.putSession("key",redisDto);

        redisUtils.deleteSession("key");

        boolean isInredis = redisUtils.isInRedis("key");

        assertFalse(isInredis);
    }

}