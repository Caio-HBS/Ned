package io.github.caiohbs.authentication.model;

import io.github.caiohbs.authentication.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;

    @Column(unique=true, nullable=false, length=100)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false, length=150)
    private String fullName;

    @Column(nullable=false)
    private LocalDate birthday;

    @Column(unique=true, nullable=false, length=100)
    private String uniqueLocalIdentification;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String userGroup;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    private boolean active = true;

    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public User(String email, String password, String fullName, LocalDate birthday,
                String uniqueLocalIdentification, String phoneNumber, List<Address> address) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.birthday = birthday;
        this.uniqueLocalIdentification = uniqueLocalIdentification;
        this.phoneNumber = phoneNumber;
        this.role = Role.USER;
        this.addresses = address;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive();
    }

}
