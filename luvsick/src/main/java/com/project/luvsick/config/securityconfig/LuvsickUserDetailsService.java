package com.project.luvsick.config.securityconfig;

import com.project.luvsick.model.User;
import com.project.luvsick.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Implementation of Spring Security's {@link UserDetailsService} that loads user-specific data.
 *
 * <p>This service retrieves a {@link User} from the database using their email address,
 * converts the user's roles/authorities into Spring Security's {@link GrantedAuthority} list,
 * and returns a {@link UserDetails} object that Spring Security uses for authentication and authorization.</p>
 *
 * <p>This class is annotated with {@link Service} to indicate that it's a Spring-managed service bean
 * and uses Lombok's {@link RequiredArgsConstructor} to automatically generate a constructor
 * for the final {@link UserRepository} dependency.</p>
 * @author  Abderahman Walid Hafez
 * @see UserDetailsService
 */
@Service
@RequiredArgsConstructor

public class LuvsickUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    /**
     * Locates the user based on the given email.
     *
     * <p>This method is called by Spring Security during authentication to load user details
     * using the email as the username. It fetches the user from the database,
     * throws {@link UsernameNotFoundException} if the user is not found,
     * and converts the user's roles/authorities to Spring Security's {@link GrantedAuthority} objects.</p>
     *
     * @param email the email of the user whose data is required
     * @return a fully populated {@link UserDetails} object (never {@code null})
     * @throws UsernameNotFoundException if the user with the specified email could not be found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User details not found for:"+email));
        List<GrantedAuthority>authorities=user
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),authorities);
    }
}
