package com.explicitUserRegistration.Service;

import com.explicitUserRegistration.Repository.ConfirmationTokenRepository;
import com.explicitUserRegistration.model.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }
}
