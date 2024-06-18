package com.explicitUserRegistration.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class PasswordResetToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1905122041950251207L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_sequence")
    @SequenceGenerator(
            name = "password_reset_sequence",
            sequenceName = "password_reset_sequence",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public PasswordResetToken(String token, LocalDateTime createdAt, LocalDateTime expireAt, User user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.user = user;
    }
}
