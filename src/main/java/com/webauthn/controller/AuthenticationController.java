package com.webauthn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webauthn.dtos.authentication.FinishAuthReqDto;
import com.webauthn.dtos.authentication.StartAuthReqDto;
import com.webauthn.dtos.authentication.StartAuthResDto;
import com.webauthn.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InvalidAttributeValueException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "/authenticate")
    public StartAuthResDto startAuthenticate(@RequestBody StartAuthReqDto reqDto) throws JsonProcessingException {
           return authenticationService.startAuthenticate(reqDto);
    }

    @PostMapping(path = "/authenticate/finish")
    public String finishAuthenticate(@RequestBody FinishAuthReqDto reqDto) throws Exception {
        return authenticationService.finishAuthenticate(reqDto);
    }
}