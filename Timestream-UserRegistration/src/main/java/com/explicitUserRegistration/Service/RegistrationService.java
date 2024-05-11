package com.explicitUserRegistration.Service;

import com.explicitUserRegistration.Dto.RegisterRequest;
import com.explicitUserRegistration.Repository.ConfirmationTokenRepository;
import com.explicitUserRegistration.Repository.UserRepository;
import com.explicitUserRegistration.Validator.EmailSender;
import com.explicitUserRegistration.Validator.EmailValidator;
import com.explicitUserRegistration.Validator.PasswordValidator;
import com.explicitUserRegistration.model.AppUserRole;
import com.explicitUserRegistration.model.ConfirmationToken;
import com.explicitUserRegistration.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrationService {

    private final EmailValidator emailValidator;
    private final UserService userService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final PasswordValidator passwordValidator;

    public RegistrationService(EmailValidator emailValidator, UserService userService,
                               ConfirmationTokenRepository confirmationTokenRepository,
                               UserRepository userRepository, EmailSender emailSender, PasswordValidator passwordValidator) {
        this.emailValidator = emailValidator;
        this.userService = userService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.passwordValidator = passwordValidator;
    }

    public String register(RegisterRequest registerRequest) {


        // check if email is valid
        boolean isValid = emailValidator.test(registerRequest.getEmail());
        if (!isValid) {
            throw new IllegalStateException("email not valid");
        }

        // check if password is valid
        boolean isValidPassword = passwordValidator.isValid(registerRequest.getPassword());
        if (!isValidPassword) {
            throw new IllegalStateException("password invalid");
        }
        String token = userService.Signup(
                new User(
                        registerRequest.getFirstname(),
                        registerRequest.getLastname(),
                        registerRequest.getEmail(),
                        registerRequest.getPassword(),
                        AppUserRole.USER
                )
        );

        String link = "http://localhost:8080/api/v1/register?token=" + token;
        emailSender.sendEmailMessage(registerRequest.getEmail(), buildEmail(registerRequest.getFirstname(), link));
        return token;
    }

    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        if (confirmationToken == null) {
            throw new IllegalStateException("invalid token");
        }

        //---- Get user and calculate the expiration time----\\
        User user = confirmationToken.getUser();
        LocalDateTime expireAt = confirmationToken.getExpireAt();
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(expireAt)) {
            confirmationTokenRepository.delete(confirmationToken);
            return "Expired";
        }
        user.setEnable(true);
        userRepository.save(user);
        return "Confirmed";
    }

    private String buildEmail(String name, String link) {
        return "Hi " + name + ",\n\nPlease click the following link to verify your account: " + link;
    }
}
