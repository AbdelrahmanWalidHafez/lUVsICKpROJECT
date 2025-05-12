package com.project.luvsick.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import io.jsonwebtoken.security.Keys;
@Slf4j
@RequiredArgsConstructor
public class JWTTokenValidationFilter extends OncePerRequestFilter {
    private final Environment env;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt =  extractJwtFromCookie(request);
        if (null != jwt) {
            try {
                String secret=env.getProperty("JWT_SECRET");
                if (secret.length() < 32) {
                    throw new IllegalStateException("JWT_SECRET is not defined or too short in application.properties (must be at least 32 characters)");
                }
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();
                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));
                Authentication authentication = new UsernamePasswordAuthenticationToken
                        (email, null, AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new BadCredentialsException("invalid Token received");
            }
        }
        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/api/v1/auth/login");
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> "JWT-TOKEN".equals(cookie.getName()))
                .findFirst();

        return jwtCookie.map(Cookie::getValue).orElse(null);
    }
}
