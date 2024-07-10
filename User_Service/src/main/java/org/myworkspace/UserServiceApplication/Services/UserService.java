package org.myworkspace.UserServiceApplication.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myworkspace.UserServiceApplication.DTOs.UserRequest;
import org.myworkspace.Utilities.CommonConstraints;
import org.myworkspace.UserServiceApplication.Entities.Users;
import org.myworkspace.UserServiceApplication.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.json.simple.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.Authority}")
    private String userAuthority;
    @Value("${admin.Authority}")
    private String adminAuthority;

    public Users addUpdateUser(UserRequest userInfo) throws JsonProcessingException {
        Users user = userInfo.toUserEntity();
        user.setAuthorities(userAuthority);
        user.setPassword(encoder.encode(userInfo.getPassword()));
        user = userRepository.save(user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstraints.USER_CONTACT_NO, user.getContactNo());
        jsonObject.put(CommonConstraints.USER_EMAIL, user.getEmail());
        jsonObject.put(CommonConstraints.USER_NAME, user.getName());
        jsonObject.put(CommonConstraints.USER_IDENTIFIER, user.getIdentifier());
        jsonObject.put(CommonConstraints.USER_IDENTIFIER_VALUE, user.getUserIdentifierValue());
        jsonObject.put(CommonConstraints.USER_ID, user.getPk());
        logger.info("json object as a string " + jsonObject);
        logger.info("json object as a string by objectMapper " + objectMapper.writeValueAsString(jsonObject));
        kafkaTemplate.send(CommonConstraints.USER_CREATED_TOPIC, objectMapper.writeValueAsString(jsonObject));
        return user;
    }

    @Override
    public Users loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByContactNo(username);
    }
}
