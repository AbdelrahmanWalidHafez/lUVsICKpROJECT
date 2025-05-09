package com.project.luvsick.mapper;

import com.project.luvsick.dto.RegisterUserRequestDTO;
import com.project.luvsick.dto.UserDTO;
import com.project.luvsick.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User toUser(RegisterUserRequestDTO registerUserRequestDTO) {
        return  User
                .builder()
                .email(registerUserRequestDTO.getEmail())
                .name(registerUserRequestDTO.getName())
                .password(passwordEncoder.encode(registerUserRequestDTO.getPassword()))
                .build();
    }

    public UserDTO toDto(User user){
        return UserDTO
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
} 