package org.myworkspace.UserServiceApplication.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.myworkspace.UserServiceApplication.DTOs.UserRequest;
import org.myworkspace.UserServiceApplication.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.myworkspace.UserServiceApplication.Entities.Users;



@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/addUpdate") // ?! separate POST request should be there for update
    private ResponseEntity<Users> addUpdate(@RequestBody @Valid UserRequest userInfo) throws JsonProcessingException {
        Users user = userService.addUpdateUser(userInfo);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
