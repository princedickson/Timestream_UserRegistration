package com.explicitUserRegistration.Repository;

import com.explicitUserRegistration.model.PasswordResetToken;
import com.explicitUserRegistration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    boolean existsByToken(String token);
}
