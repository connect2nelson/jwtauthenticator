package com.fun.abm.JWTAuthenticator.controller;

import com.fun.abm.JWTAuthenticator.annotation.Authenticated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserGreetingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserGreetingController.class);

    @Authenticated
    @GetMapping("/{userId}")
    public String greetUser(@PathVariable("userId") String userId) {
        return "Hello, " + userId;
    }
}
