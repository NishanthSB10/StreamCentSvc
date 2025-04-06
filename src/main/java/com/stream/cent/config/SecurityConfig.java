package com.stream.cent.config;

import com.stream.cent.domain.RefreshToken;
import com.stream.cent.filter.JwtRequestFilter;
import com.stream.cent.repository.UserRepository;
import com.stream.cent.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtRequestFilter jwtRequestFilter;

    private final UserRepository userRepository;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter,
                          AuthenticationProvider authenticationProvider,
                          UserRepository userRepository) {
        this.authenticationProvider = authenticationProvider;
        this.jwtRequestFilter = jwtRequestFilter;
        this.userRepository = userRepository;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/auth/**", "/error").permitAll()
                        .requestMatchers("/hello").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            Optional<Cookie> oldRefreshTokenCookie = Arrays.stream(request.getCookies())
                                    .filter(cookie -> appConfig.getRefreshTokenCookieName().equals(cookie.getName()))
                                    .findFirst();

                            String refreshToken = oldRefreshTokenCookie.map(Cookie::getValue).orElse(null);

                            Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshToken);
                            if(refreshTokenOptional.isPresent()){
                                RefreshToken refreshTokenObj = refreshTokenOptional.get();
                                refreshTokenService.deleteByUserId(refreshTokenObj.getUser());
                            }

                            Cookie jwtCookie = new Cookie(appConfig.getJwtCookieName(), null);
                            jwtCookie.setMaxAge(0);
                            jwtCookie.setPath("/");
                            jwtCookie.setHttpOnly(true);
                            jwtCookie.setSecure(true);
                            response.addCookie(jwtCookie);

                            Cookie refreshTokenCookie = new Cookie(appConfig.getRefreshTokenCookieName(), null);
                            refreshTokenCookie.setMaxAge(0);
                            refreshTokenCookie.setPath("/");
                            refreshTokenCookie.setHttpOnly(true);
                            refreshTokenCookie.setSecure(true);
                            response.addCookie(refreshTokenCookie);

                        })
                );
        return http.build();
    }


}

