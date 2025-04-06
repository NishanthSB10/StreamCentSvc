package com.stream.cent.controller;

import com.stream.cent.domain.User;
import com.stream.cent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class MyNewController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public Map<String, String> helloWorld(){
        Optional<User> user = userRepository.findByUsername("admin");
        Map<String, String> response = new HashMap<>();
        if(!user.isEmpty()){
            response.put("password", user.get().getPassword());
        }
        return response;
    }
}
