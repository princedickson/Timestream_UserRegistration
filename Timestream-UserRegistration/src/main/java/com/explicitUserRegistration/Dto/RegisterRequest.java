package com.explicitUserRegistration.Dto;

import com.explicitUserRegistration.Validator.ValidPassword;
import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class RegisterRequest {
    private  String firstname;
    private  String lastname;
    private  String email;
    @ValidPassword
    private  String password;
}
