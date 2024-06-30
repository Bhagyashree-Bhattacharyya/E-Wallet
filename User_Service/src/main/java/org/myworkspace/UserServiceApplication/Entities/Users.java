package org.myworkspace.UserServiceApplication.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    @Column(unique = true, nullable = false)
    private String contactNo;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private String dob;
    @Enumerated
    private UserIdentifier identifier;
    private String userIdentifierValue;
    private UserType type;

    private String authorities;
    private String password;
    private boolean accountNonExpired;
    private boolean enabled;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    @CreationTimestamp
    private Date onboardedOn;
    @UpdateTimestamp
    private Date updatedOn;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream((authorities.split(","))).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return contactNo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
