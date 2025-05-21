package com.project.luvsick.config.securityconfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
/**
 * Custom authentication provider for Luvsick application that
 * authenticates users based on username (email) and password.
 * <p>
 * This provider uses a {@link UserDetailsService} to load user details
 * and a {@link PasswordEncoder} to verify the password.
 * </p>
 * @author Abdelrahman Walid Hafez
 */@Component
@RequiredArgsConstructor
public class LuvsickUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;

    private  final PasswordEncoder passwordEncoder;
    /**
     * Authenticates a user based on username and password.
     * <p>
     * Loads user details by username (email) and compares the provided password
     * with the stored encoded password using {@link PasswordEncoder#matches}.
     * If authentication is successful, returns an authenticated
     * {@link UsernamePasswordAuthenticationToken} with authorities.
     * Otherwise, throws {@link BadCredentialsException}.
     * </p>
     *
     * @param authentication the authentication request object containing credentials
     * @return a fully authenticated object including authorities
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        String email=authentication.getName();
        String password=authentication.getCredentials().toString();
        UserDetails userDetails= userDetailsService.loadUserByUsername(email);
        if (passwordEncoder.matches(password, userDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(email,null,userDetails.getAuthorities());
        }
        throw new BadCredentialsException("invalid username or password");
    }
    /**
     * Indicates whether this provider supports the indicated authentication type.
     *
     * @param authentication the class of the authentication object
     * @return true if {@link UsernamePasswordAuthenticationToken} or subclass is supported, false otherwise
     */
    @Override
    public boolean supports(Class<?> authentication){
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
