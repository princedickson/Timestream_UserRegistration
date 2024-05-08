package com.explicitUserRegistration.Controller;

import com.explicitUserRegistration.Dto.RegisterRequest;
import com.explicitUserRegistration.Service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String register(@RequestBody RegisterRequest registerRequest) {
        return registrationService.register(registerRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String verifyRegistration(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
