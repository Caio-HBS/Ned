package io.github.caiohbs.authentication.model;

import io.github.caiohbs.authentication.model.enums.UserTokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user_tokens")
@EntityListeners(AuditingEntityListener.class)
public class UserToken {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long tokenId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private UserTokenType tokenType;
    private String token;
    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime usedAt = null;

    private boolean active = true;

    public UserToken(UserTokenType tokenType, String token, LocalDateTime expiresAt) {
        this.tokenType = tokenType;
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
