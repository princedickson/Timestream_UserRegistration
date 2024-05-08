package com.explicitUserRegistration.Service;

import com.explicitUserRegistration.Repository.UserRepository;
import com.explicitUserRegistration.model.ConfirmationToken;
import com.explicitUserRegistration.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private ConfirmationTokenService confimationTokenService;
    private final static String USER_NOT_FOUND = " user with email %s not found";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow
                (() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    public String Signup(User user){
        boolean UserExist = userRepository.findByEmail(user.getEmail()).isPresent();
        if(UserExist){
            // TODO: if email not confirmed send confirmation mail
            throw  new IllegalStateException("email already exist");
        }
        String  encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Send confirmation token
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                user

        );
        confimationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }
}
