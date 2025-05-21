package com.project.luvsick.mapper;

import com.project.luvsick.dto.RegisterUserRequestDTO;
import com.project.luvsick.dto.UserDTO;
import com.project.luvsick.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Abdelrahman Walid Hafez
 */
@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    /**
     * Converts a {@link RegisterUserRequestDTO} to a {@link User} entity.
     * The password from the DTO is encoded using the provided {@code passwordEncoder}.
     *
     * @param registerUserRequestDTO the DTO containing user registration data
     * @return a {@link User} entity with encoded password and other details set
     */
    public User toUser(RegisterUserRequestDTO registerUserRequestDTO) {
        return  User
                .builder()
                .email(registerUserRequestDTO.getEmail())
                .name(registerUserRequestDTO.getName())
                .password(passwordEncoder.encode(registerUserRequestDTO.getPassword()))
                .build();
    }

    /**
     * Converts a {@link User} entity to a {@link UserDTO} data transfer object.
     *
     * @param user the user entity to convert
     * @return a {@link UserDTO} with user details (UUID, email, name)
     */
    public UserDTO toDto(User user){
        return UserDTO
                .builder()
                .uuid(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
} 