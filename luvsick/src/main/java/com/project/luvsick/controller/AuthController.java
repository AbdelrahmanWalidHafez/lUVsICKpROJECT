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
import org.springframework.http.CacheControl;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * REST controller for handling authentication-related operations such as login, logout, and registration.
 * @author Abdelrahman Walid Hafez
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    /**
     * Authenticates a user using the provided login credentials.
     * <p>
     * This endpoint expects a {@link LoginRequestDTO} containing the user's email and password.
     * If the authentication is successful, it sets an HTTP-only cookie named "JWT-TOKEN" in the response.
     * </p>
     *
     * @param loginRequestDTO The login request payload containing email and password (validated).
     * @param response        The HTTP servlet response used to add the JWT cookie.
     * @return A ResponseEntity with a success message if authentication is successful.
     */
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
    /**
     * Registers a new user with the provided registration details.
     * <p>
     * This endpoint accepts a {@link RegisterUserRequestDTO} containing the user's name, email, password,
     * and authority role. It checks if a user with the provided email already exists.
     * If not, it maps the request DTO to a {@link User} entity, assigns the authority, and persists it to the database.
     * </p>
     *
     * @param registerUserRequestDTO The registration request payload containing user information (validated).
     * @return A {@link ResponseEntity} containing the created {@link UserDTO} and HTTP status 201 (Created).
     * @throws EntityExistsException if a user with the provided email already exists.
     */
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
    /**
     * Logs out the user by clearing all cookies associated with the request.
     * <p>
     * This method retrieves all cookies from the incoming {@link HttpServletRequest},
     * sets their values to {@code null}, and sets their max age to 0 to effectively delete them.
     * Each cleared cookie is re-added to the {@link HttpServletResponse}.
     * </p>
     *
     * @param request  The HTTP request containing the cookies to clear.
     * @param response The HTTP response used to send the cleared cookies back to the client.
     * @return A {@link ResponseEntity} with a message indicating that logout was successful.
     */
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
    /**
     * Retrieves a list of all registered users.
     * <p>
     * This endpoint fetches all {@link User} entities from the database,
     * maps them to {@link UserDTO} objects using {@code userMapper}, and returns the list.
     * The response is cached for 5 minutes using HTTP cache control headers.
     * </p>
     *
     * @return A {@link ResponseEntity} containing a list of {@link UserDTO} and a cache control directive.
     */
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>>getAllUsers(){
        List<UserDTO> userDTOS=userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.SECONDS))
                .body(userDTOS);
    }
    /**
     * Deletes a user by their unique identifier (UUID).
     * <p>
     * This endpoint removes the user associated with the specified UUID from the database.
     * If no user is found, {@link org.springframework.data.jpa.repository.JpaRepository#deleteById(Object)}
     * will throw an exception.
     * </p>
     *
     * @param uuid The unique identifier of the user to be deleted.
     * @return A {@link ResponseEntity} with HTTP status {@code 204 No Content} if the deletion is successful.
     */
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid){
        userRepository.deleteById(uuid);
        log.info("deleted a user");
        return ResponseEntity.noContent().build();
    }
}
