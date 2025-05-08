package com.project.luvsick.service;

import com.project.luvsick.repo.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final Environment env;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Override
    public String authenticate(String email, String password) {
        Authentication authentication= UsernamePasswordAuthenticationToken.unauthenticated(email,password);
        Authentication authenticationResponse=authenticationManager.authenticate(authentication);
        if (null!=authenticationResponse&&authenticationResponse.isAuthenticated()){
            String secret=env.getProperty("JWT_SECRET");
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            log.info(authenticationResponse.getAuthorities().toString());
            return Jwts
                    .builder()
                    .issuer("luvsick").subject("JWT Token")
                    .claim("email", authenticationResponse.getName())
                    .claim("authorities", authenticationResponse
                            .getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                    .issuedAt(new java.util.Date())
                    .expiration(new java.util.Date((new java.util.Date()).getTime() + 28_000_000))
                    .signWith(secretKey).compact();
        }
        throw new BadCredentialsException("Invalid email or password");
    }
}
