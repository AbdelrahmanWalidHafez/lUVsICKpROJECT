package com.project.luvsick.service;

/**
 * @author Abdelrahman Walid Hafez
 */
public interface AuthService {
     /**
      * Authenticates a user using their email and password.
      *
      * @param email    the user's email address used for authentication
      * @param password the user's password
      * @return a JWT token string if authentication is successful
      * @throws org.springframework.security.authentication.BadCredentialsException if the authentication fails due to invalid credentials
      */
     String authenticate(String email,String password);
}
