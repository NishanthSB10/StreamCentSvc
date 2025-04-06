package com.stream.cent.filter;

import com.stream.cent.config.AppConfig;
import com.stream.cent.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private AppConfig appConfig;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    private static final List<String> PERMIT_ALL_PATHS = List.of("/login", "/register", "/css/", "/js/", "/auth/", "/error");


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        requestURI = requestURI.replace(appConfig.getContextPath(), "");

        // Skip filter for permitAll paths
        if (PERMIT_ALL_PATHS.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> appConfig.getJwtCookieName().equals(cookie.getName()))
                .findFirst();

        Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> appConfig.getRefreshTokenCookieName().equals(cookie.getName()))
                .findFirst();

        String jwtToken = jwtCookie.map(Cookie::getValue).orElse(null);
        String refreshToken = refreshTokenCookie.map(Cookie::getValue).orElse(null);

        if (jwtToken == null || jwtToken.isEmpty()) {
            if(refreshToken == null || refreshToken.isEmpty()){
                filterChain.doFilter(request, response);
                return;
            }
            else{
                jwtToken = jwtUtil.refreshJwtToken(refreshToken, response);
            }

        }

        try {
            final String jwt = jwtToken;
            final String userEmail = jwtUtil.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    // Extract authorities from JWT token
                    List<SimpleGrantedAuthority> authorities = jwtUtil.extractAuthorities(jwt);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities // Use extracted authorities instead of userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                else{
                    jwtUtil.refreshJwtToken(refreshToken, response);
                    List<SimpleGrantedAuthority> authorities = jwtUtil.extractAuthorities(jwt);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities // Use extracted authorities instead of userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\": \"Invalid or expired JWT token\"}");
//            response.getWriter().flush();
//            return;
        }
    }
}

