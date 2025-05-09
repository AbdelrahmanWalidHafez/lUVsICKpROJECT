package com.project.luvsick.controller;

import com.project.luvsick.dto.LoginRequestDTO;
import com.project.luvsick.dto.RegisterUserRequestDTO;
import com.project.luvsick.dto.UserDTO;
import com.project.luvsick.mapper.UserMapper;
import com.project.luvsick.model.Authority;
import com.project.luvsick.model.User;
import com.project.luvsick.repo.UserRepository;
import com.project.luvsick.service.AuthService;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        if(userRepository.findByEmail(registerUserRequestDTO.getEmail()).isPresent()){
            throw new EntityExistsException("this user already exists");
        }
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
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>>getAllUsers(){
        List<UserDTO> userDTOS=userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOS);
    }
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid){
        userRepository.deleteById(uuid);
        log.info("deleted a user");
        return ResponseEntity.noContent().build();
    }
}
