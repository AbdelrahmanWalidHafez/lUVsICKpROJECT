package com.project.luvsick.securityconfig;

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

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class ProjectConfig {
    private final Environment environment;
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
                .csrf(httpSecurityCsrfConfigurer -> {
                    httpSecurityCsrfConfigurer
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
                            "/api/v1/order/**"
                        );
                })
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
                )
                .exceptionHandling(
                        httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return httpSecurity.build();
    }
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder){
        LuvsickUsernamePasswordAuthenticationProvider luvsickUsernamePasswordAuthenticationProvider=
                new LuvsickUsernamePasswordAuthenticationProvider(
                userDetailsService
                ,passwordEncoder);
        return new ProviderManager(luvsickUsernamePasswordAuthenticationProvider);
    }
}
