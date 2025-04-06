package com.stream.cent.utils;

import com.stream.cent.config.AppConfig;
import com.stream.cent.domain.RefreshToken;
import com.stream.cent.domain.User;
import com.stream.cent.repository.UserRepository;
import com.stream.cent.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, appConfig.getJwtExpiration());
    }

    public long getExpirationTime() {
        return appConfig.getJwtExpiration();
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        // Ensure authorities are prefixed with "ROLE_"
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(authority -> "ROLE_" + authority.getAuthority())  // Ensure correct format
                .collect(Collectors.toList());

        // Store the updated authorities list in extraClaims
        extraClaims.put("authorities", authorities);

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(appConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("authorities", List.class);

        if (roles == null) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String generateJwtToken(HttpServletResponse response, String userName){
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        String jwt = this.generateToken(userDetails);

        Optional<User> users = userRepository.findByUsername(userDetails.getUsername());
        if(users.isPresent()){
            refreshTokenService.deleteByUserId(users.get());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(users.get());
            Cookie refreshTokenCookie = new Cookie(appConfig.getRefreshTokenCookieName(), refreshToken.getToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setMaxAge(86400);

            response.addCookie(refreshTokenCookie); // 24 hours expiration
        }


        Cookie jwtCookie = new Cookie(appConfig.getJwtCookieName(), jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(900); // 15 minutes expiration

        response.addCookie(jwtCookie);

        return jwt;
    }

    public String refreshJwtToken(String refreshToken, HttpServletResponse response) throws IOException {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshToken);
        if(refreshTokenOptional.isPresent()){
            RefreshToken refreshTokenObj = refreshTokenOptional.get();
            refreshTokenService.verifyExpiration(refreshTokenObj);
            String jwtToken = generateJwtToken(response, refreshTokenObj.getUser().getUsername());
            return jwtToken;

        }
        else{
            return null;
        }

//        String jwtToken = refreshTokenService.findByToken(refreshToken)
//                .map(refreshTokenService::verifyExpiration)
//                .map(RefreshToken::getUser)
//                .map(user -> {
//                    String newJwtToken = generateJwtToken(response);
//                    return newJwtToken;
//                })
//                .orElseThrow(() -> new RefreshTokenException(refreshToken,
//                        "Refresh token is not in database!"));
//        return jwtToken;
    }
}

