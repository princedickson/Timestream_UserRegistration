package com.explicitUserRegistration.Dto;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class PasswordReset {

    private String token;
    private String newPassword;
}
