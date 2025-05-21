package com.project.luvsick.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom access dined handler
 * @author Abdelrahman Walud
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * Handles access denied exception by setting the HTTP response status to 403 (Forbidden),
     * adding a custom header, and writing a JSON error message body with details.
     *
     * @param request                 the HTTP servlet request
     * @param response                the HTTP servlet response
     * @param accessDeniedException   the exception indicating the access denial
     * @throws IOException            if an input or output exception occurs
     * @throws ServletException       if a servlet exception occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LocalDateTime currentTimeStamp= LocalDateTime.now();
        String message= (accessDeniedException != null && accessDeniedException.getMessage() != null) ?
                accessDeniedException.getMessage() : "Authorization failed";
        String path = request.getRequestURI();
        response.setHeader("luvsick-denied-reason", "Authorization failed");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        // Construct the JSON response
        String jsonResponse =
                String.format("{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                        currentTimeStamp, HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        message, path);
        response.getWriter().write(jsonResponse);
    }
}
