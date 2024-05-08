package com.explicitUserRegistration.Dto;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class RegisterRequest {
    private  String firstname;
    private  String lastname;
    private  String email;
    private  String password;
}
