package com.webauthn.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webauthn.dtos.registration.RedisDto;
import com.yubico.webauthn.data.ByteArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class RedisUtils {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * Redis Session에 값을 넣는다.
     *
     * @param key (Base64 Encoded id)
     * @param vo
     */
    public void putSession(String key, RedisDto vo) throws JsonProcessingException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = objectMapper.writeValueAsString(vo);
        valueOperations.set(key, value);
        redisTemplate.expireAt(key, Date.from(ZonedDateTime.now().plusHours(10).toInstant())); // Todo Redis파기 10시간으로 늘림
        log.debug("putSession() && Key is {}, Value is {}", key, value);
    }

    /**
     * Redis내 key값과 Registration session내 저장된 값이 일치한지 검사
     */
    public boolean isRegisterRequestIdInRedis(String key) throws JsonProcessingException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        RedisDto outpVo = objectMapper.readValue(value, RedisDto.class);

        return key.equals(outpVo.getRegistrationResponse().getRequestId().getBase64());
    }

    /**
     * Redis내 key값과 Authenticate session내 저장된 값이 일치한지 검사
     */
    public boolean isAuthenticateRequestIdInRedis(String key) throws JsonProcessingException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        RedisDto outpVo = objectMapper.readValue(value, RedisDto.class);

        return key.equals(outpVo.getAuthenticateResponse().getRequestId().getBase64());
    }

    /**
     * 해당 Key값에 해당하는 세션이 저장되어있는지 여부에 대해 Boolean Type 반환
     * @param key
     * @return
     * @throws JsonProcessingException
     */
    public boolean isInRedis(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);

        return !ObjectUtils.isEmpty(value);
    }

    /**
     * Redis Session에서 Key에 해당하는 값을 추출한다.
     *
     * @param key
     * @return
     * @throws JsonProcessingException
     */
    public RedisDto getSession(String key) throws JsonProcessingException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        RedisDto outpVo = objectMapper.readValue(value, RedisDto.class);

        log.debug("getSession() Return is {}", outpVo);
        return outpVo;
    }

    /**
     * Key값으로 조회한 Redis세션 내에서 Token값의 존재 여부 검사
     * @param key
     * @param token
     * @return
     */
    public boolean isSessionForUser(String key, String token) throws JsonProcessingException {

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        RedisDto outpVo = objectMapper.readValue(value, RedisDto.class);

        ByteArray storedToken = outpVo.getRegistrationResponse().getSessionToken();
        String storedTokenString = storedToken.getBase64();
        return token.equals(storedTokenString);

    }

    public void deleteSession(String key) {
        redisTemplate.delete(key);
    }

}
