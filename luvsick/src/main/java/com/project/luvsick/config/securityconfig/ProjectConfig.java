package com.project.luvsick.config.securityconfig;

import com.project.luvsick.filter.CsrfCookieFilter;
import com.project.luvsick.filter.JWTTokenValidationFilter;

import com.project.luvsick.handler.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import java.util.Collections;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
/**
 * Security configuration class for the Luvsick project.
 *
 * <p>This class defines the Spring Security filter chain, CORS and CSRF settings,
 * session management, authentication manager, password encoding, and access control rules.
 * It also registers custom filters and exception handlers.</p>
 *
 * @author Abdelrahman Walid Hafez
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class ProjectConfig {
    private final  Environment environment;
    /**
     * Defines the main security filter chain for HTTP requests.
     *
     * <p>Configures CORS policy, CSRF protection, session management, authorization rules,
     * custom filters for JWT validation and CSRF token handling, and exception handling.</p>
     *
     * @param httpSecurity the {@link HttpSecurity} object to configure security
     * @return the built {@link SecurityFilterChain}
     * @throws Exception in case of configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{
        httpSecurity
                .anonymous(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
                        .configurationSource(request -> {
                            CorsConfiguration corsConfiguration=new CorsConfiguration();
                            corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                            corsConfiguration.setAllowCredentials(true);
                            corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                            corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                            corsConfiguration.setMaxAge(3600L);
                        return corsConfiguration;
                        }))
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                        "/error",
                        "/api/v1/category/getCategories",
                        "/api/v1/product/allProducts**",
                        "/api/v1/allProducts**",
                        "/api/v1/product/image/**",
                        "/api/v1/auth/login",
                        "/api/v1/order/createOrder",
                        "/api/v1/auth/getAllUsers",
                        "/api/v1/product/newArrivals",
                        "/api/v1/order/getOrders**",
                        "/api/v1/order/**",
                            "/swagger-ui/**"
                    ))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JWTTokenValidationFilter(environment), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/auth/register").hasAnyRole("OWNER","MANAGER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/auth/*").hasAnyRole("OWNER","MANAGER")
                        .requestMatchers("/api/v1/auth/getAllUsers").hasAnyRole("OWNER","MANAGER")
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v1/category/addCategory").hasAnyRole("OWNER","MANAGER")
                        .requestMatchers("/api/v1/category/getCategories").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/category/**").hasAnyRole("OWNER","MANAGER")
                        .requestMatchers("/api/v1/product/addProduct").hasAnyRole("OWNER","MANAGER")
                        .requestMatchers("/api/v1/product/newArrivals").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/product/**").hasAnyRole("OWNER","MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/product/**").hasAnyRole("OWNER","MANAGER")
                        .requestMatchers("/api/v1/product/allProducts**").permitAll()
                        .requestMatchers("/api/v1/allProducts**").permitAll()
                        .requestMatchers("/api/v1/product/image/**").permitAll()
                        .requestMatchers("/api/v1/order/createOrder").permitAll()
                        .requestMatchers("/api/v1/order/getOrders**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/v1/order/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(
                        httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return httpSecurity.build();
    }
    /**
     * Password encoder bean to provide secure password hashing.
     *
     * <p>Creates a delegating password encoder that supports multiple encoding algorithms.</p>
     *
     * @return the {@link PasswordEncoder} instance
     */
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    /**
     * Authentication manager bean configured with a custom username/password authentication provider.
     *
     * @param userDetailsService service to load user details by username (email)
     * @param passwordEncoder password encoder used to validate passwords
     * @return the configured {@link AuthenticationManager}
     */
    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder){
        LuvsickUsernamePasswordAuthenticationProvider luvsickUsernamePasswordAuthenticationProvider=
                new LuvsickUsernamePasswordAuthenticationProvider(
                userDetailsService
                ,passwordEncoder);
        return new ProviderManager(luvsickUsernamePasswordAuthenticationProvider);
    }
}
