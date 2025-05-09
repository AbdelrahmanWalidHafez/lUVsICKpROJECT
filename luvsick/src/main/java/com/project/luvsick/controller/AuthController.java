package com.project.luvsick.controller;

import com.project.luvsick.dto.LoginRequestDTO;
import com.project.luvsick.dto.RegisterUserRequestDTO;
import com.project.luvsick.dto.UserDTO;
import com.project.luvsick.mapper.UserMapper;
import com.project.luvsick.model.Authority;
import com.project.luvsick.model.User;
import com.project.luvsick.repo.UserRepository;
import com.project.luvsick.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response){
        String jwt = authService.authenticate(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        Cookie cookie = new Cookie("JWT-TOKEN", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(28800);
        response.addCookie(cookie);
        return ResponseEntity.ok("successful login");
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterUserRequestDTO registerUserRequestDTO){
        User user=userMapper.toUser(registerUserRequestDTO);
        Authority authority=new Authority("ROLE_"+registerUserRequestDTO.getAuthorityName());
        user.setAuthorities(Set.of(authority));
        authority.setUser(user);
        user=userRepository.save(user);
        log.info("user:{} is created", user.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));

    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setValue(null);
                    cookie.setPath("/");
                    cookie.setHttpOnly(cookie.isHttpOnly());
                    cookie.setSecure(false);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    log.debug("Cleared cookie: {}", cookie.getName());
                }
            }
            SecurityContextHolder.clearContext();
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("Clear-Site-Data", "\"cookies\", \"storage\"");
            return ResponseEntity.ok("Logout Successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
