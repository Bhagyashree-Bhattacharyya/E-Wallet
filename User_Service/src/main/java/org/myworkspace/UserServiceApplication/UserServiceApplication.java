package org.myworkspace.UserServiceApplication;

import org.myworkspace.UserServiceApplication.Entities.UserType;
import org.myworkspace.UserServiceApplication.Entities.Users;
import org.myworkspace.UserServiceApplication.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Value("${service.Authority}")
	private String serviceAuthority;
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Users user = Users.builder().contactNo("txn-service")
				.enabled(true).accountNonLocked(true).credentialsNonExpired(true).accountNonExpired(true)
				.password(passwordEncoder.encode("txn-service")).type(UserType.SERVICE)
				.email("txn-service@gmail.com").authorities(serviceAuthority).build();
		userRepository.save(user);
	}
}
