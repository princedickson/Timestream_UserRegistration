package com.explicitUserRegistration.Service;

import com.explicitUserRegistration.Dto.RegisterRequest;
import com.explicitUserRegistration.Repository.ConfirmationTokenRepository;
import com.explicitUserRegistration.Repository.PasswordTokenRepository;
import com.explicitUserRegistration.Repository.UserRepository;
import com.explicitUserRegistration.Validator.EmailSender;
import com.explicitUserRegistration.Validator.EmailValidator;
import com.explicitUserRegistration.Validator.PasswordValidator;
import com.explicitUserRegistration.model.AppUserRole;
import com.explicitUserRegistration.model.ConfirmationToken;
import com.explicitUserRegistration.model.PasswordResetToken;
import com.explicitUserRegistration.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {

    private final EmailValidator emailValidator;
    private final UserService userService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final PasswordValidator passwordValidator;
    private final PasswordTokenRepository passwordTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    public RegistrationService(EmailValidator emailValidator, UserService userService,
                               ConfirmationTokenRepository confirmationTokenRepository,
                               UserRepository userRepository, EmailSender emailSender, PasswordValidator passwordValidator, PasswordTokenRepository passwordTokenRepository) {
        this.emailValidator = emailValidator;
        this.userService = userService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.passwordValidator = passwordValidator;
        this.passwordTokenRepository = passwordTokenRepository;
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
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("invalid token"));

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


    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Email not found"));

        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        passwordTokenRepository.findByUser(user)
                .ifPresent(passwordTokenRepository::delete);

        String token;
        do {
            token = UUID.randomUUID().toString().replace("-", "");
        } while (passwordTokenRepository.existsByToken(token));
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(10),
                user
        );
        passwordTokenRepository.save(passwordResetToken);
        logger.info("Password reset token generated: {}", token);

        String link = "http://localhost:8080/api/v1/register/savePassword?token=" + token;
        emailSender.sendEmailMessage(email, buildPasswordResetEmail(user.getFirstname(), link));
        logger.info("Password reset email sent to: {}", email);
    }

    @Transactional
    public String resetPassword(String token, String newPassword) {

        logger.debug("Received token: {}", token);
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("invalid token"));
        logger.info("Token found in DB: {}", passwordResetToken.getToken()); // Log the token found in the DB

        if (passwordResetToken.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }
        User user = passwordResetToken.getUser();
        boolean isValid = passwordValidator.isValid(newPassword);
        if (!isValid) {
            throw new IllegalStateException("password is not valid");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);
        passwordTokenRepository.delete(passwordResetToken);
        return token;
    }

    private String buildPasswordResetEmail(String name, String link) {
        return "Hi " + name + ",\n\nPlease click the following link to reset your password: " + link;
    }
}

