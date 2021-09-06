package com.webauthn.config;

import com.webauthn.domain.CredentialEntity;
import com.webauthn.domain.UserEntity;
import com.webauthn.dtos.entity.CredentialDto;
import com.webauthn.dtos.entity.UserDto;
import com.webauthn.repository.CredentialsRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.host}")
    private String redisHost;

    private final ModelMapper modelMapper = new ModelMapper();

    // REDIS
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory(redisHost,redisPort);
    }

    @Bean
    public RedisTemplate<?,?> redisTemplate(){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public ModelMapper userMapper(){
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD);
        modelMapper.addMappings(creMap);
        return modelMapper;
    }

    PropertyMap<CredentialEntity, CredentialDto> creMap = new PropertyMap<CredentialEntity, CredentialDto>(){
        protected void configure(){
           skip(destination.getUserId());
        }
    };
}
