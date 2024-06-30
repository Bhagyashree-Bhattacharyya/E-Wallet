package org.myworkspace.UserServiceApplication.Services;

import org.myworkspace.UserServiceApplication.DTOs.UserRequest;
import org.myworkspace.UserServiceApplication.Entities.Users;
import org.myworkspace.UserServiceApplication.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Value("${user.Authority}")
    private String userAuthority;
    @Value("${admin.Authority}")
    private String adminAuthority;

    public Users addUpdateUser(UserRequest userInfo) {
        Users user = userInfo.toUserEntity();
        user.setAuthorities(userAuthority);
        user.setPassword(encoder.encode(userInfo.getPassword())); // ?! Not Working !!
        // wallerService, NotificationService
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
