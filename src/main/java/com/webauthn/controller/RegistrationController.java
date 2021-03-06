package com.webauthn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webauthn.dtos.registration.FinishReqDto;
import com.webauthn.dtos.registration.FinishResDto;
import com.webauthn.dtos.registration.RegReqDto;
import com.webauthn.dtos.registration.RegResDto;
import com.webauthn.service.RegistrationService;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InvalidAttributeValueException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(path = "/registration")
    public RegResDto startRegistration (
            @RequestBody RegReqDto requestBody) throws InvalidAttributeValueException, JsonProcessingException {
      log.debug("body is {}",requestBody);

      return registrationService.startRegistration(requestBody);
    };

    @PostMapping(path = "/registration/finish")
    public FinishResDto finishRegistration(
            @RequestBody FinishReqDto requestBody
            ) throws JsonProcessingException, InvalidAttributeValueException, RegistrationFailedException {
        return registrationService.finishRegistration(requestBody);
    }

}
