package com.stream.cent.service;

import com.stream.cent.config.AppConfig;
import com.stream.cent.domain.User;
import com.stream.cent.dto.LoginUser;
import com.stream.cent.dto.RegisterUser;
import com.stream.cent.repository.UserRepository;
import com.stream.cent.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailSenderService mailSenderService;
    @Autowired
    private RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> loginUser(LoginUser loginUser, HttpServletResponse response){
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getUserName(), loginUser.getPassword()));

        // Generate and return JWT token
        String jwt = jwtUtil.generateJwtToken(response, loginUser.getUserName());

        return ResponseEntity.ok(Collections.singletonMap("token", jwt));
    }

    public String registerUser(RegisterUser registerUser){
        if(registerUser != null){
            Optional<User> userOptional = userRepository.findByUsername(registerUser.getUserName());
            if (userOptional.isPresent()) {
                return "Username already taken!";
            }

            User user = new User();
            user.setUsername(registerUser.getUserName());
            user.setPassword(passwordEncoder.encode(registerUser.getPassword()));
            user.setEmail(registerUser.getEmail());
            user.setRole("ADMIN"); // Default role

            userRepository.save(user);
            mailSenderService.sendEmail(registerUser.getEmail(), "Registration Successful",
                    "You have been registered successfully!");

        }
        return "Successfully registered";
    }

}
