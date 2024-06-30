package org.myworkspace.UserServiceApplication.Configurations;

import org.myworkspace.UserServiceApplication.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.net.Authenticator;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserService userService;
    @Autowired
    private CommonConfig commonConfig;

    @Value("${user.Authority}")
    private String userAuthority;
    @Value("${admin.Authority}")
    private String adminAuthority;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(commonConfig.getEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/user/addUpdate/**").permitAll()
                .anyRequest().permitAll() // ** request matchers later
        ).formLogin(withDefaults()).httpBasic(withDefaults()).csrf(csrf -> csrf.disable());
        return http.build();
    }
}
