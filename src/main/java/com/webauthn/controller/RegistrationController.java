package com.webauthn.controller;

import com.webauthn.dtos.RegReqDto;
import com.webauthn.dtos.RegResDto;
import com.webauthn.service.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.DynAnyPackage.InvalidValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InvalidAttributeValueException;

@RestController
@Slf4j
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(path = "/registration")
    public RegResDto startRegistration (
            @RequestBody RegReqDto requestBody) throws InvalidAttributeValueException {
      log.debug("body is {}",requestBody);

      return registrationService.startRegistration(requestBody);
    };

}
