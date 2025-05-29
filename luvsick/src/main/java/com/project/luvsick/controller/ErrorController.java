package com.project.luvsick.controller;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.project.luvsick.dto.ApiErrorResponse;
import com.project.luvsick.exception.InsufficientStockException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Global error handling controller for the Luvsick project.
 * <p>
 * This class intercepts exceptions thrown by controllers under the
 * "com.project.luvsick.controller" package and returns standardized
 * {@link ApiErrorResponse} objects with appropriate HTTP status codes
 * and messages.
 * <p>
 * It provides handlers for common exceptions like validation errors,
 * authentication failures, JSON parsing errors, resource not found,
 * access denied, and others.
 * </p>
 * @author Abdelrahman Walid Hafez\
 *
 */

@Hidden
@RestControllerAdvice(basePackages = "com.project.luvsick.controller")
@Slf4j
public class ErrorController {
    /**
     * Handles all uncaught exceptions.
     *
     * @param ex      the exception
     * @param request the HTTP servlet request
     * @return a response entity containing a generic internal server error message
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Handles exceptions related to invalid JSON in the request body.
     *
     * @param ex      the exception (JSON parse or mapping error)
     * @param request the HTTP servlet request
     * @return a response entity with bad request status and error message about JSON format
     */
    @ExceptionHandler({JsonParseException.class, JsonMappingException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> handleJsonParseException(Exception ex, HttpServletRequest request) {
        log.error("JSON parsing error", ex);
        String message = "Invalid JSON format. Please check your request body.";
        if (ex instanceof HttpMessageNotReadableException) {
            message = "Invalid request body format. Please ensure your JSON is properly formatted.";
        }
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles illegal argument exceptions typically thrown when method
     * parameters are invalid.
     *
     * @param ex      the illegal argument exception
     * @param request the HTTP servlet request
     * @return a response entity with bad request status and exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Invalid argument", ex);
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles illegal state exceptions which represent conflicts in
     * the application state.
     *
     * @param ex      the illegal state exception
     * @param request the HTTP servlet request
     * @return a response entity with conflict status and exception message
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.error("Invalid state", ex);
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    /**
     * Handles authentication failures such as bad credentials or username not found.
     *
     * @param ex      the authentication exception
     * @param request the HTTP servlet request
     * @return a response entity with unauthorized status and appropriate message
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        log.error("Authentication failed", ex);
        String message = "Invalid email or password";
        if (ex instanceof UsernameNotFoundException) {
            message = "User not found with the provided email";
        }
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    /**
     * Handles access denied exceptions when a user tries to access unauthorized resources.
     *
     * @param ex      the access denied exception
     * @param request the HTTP servlet request
     * @return a response entity with forbidden status and message
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("Access denied", ex);
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("You don't have permission to access this resource")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    /**
     * Handles entity not found exceptions when requested resource does not exist.
     *
     * @param ex      the entity not found exception
     * @param request the HTTP servlet request
     * @return a response entity with not found status and exception message
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFoundState(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("Entity not found", ex);
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    /**
     * Handles HTTP method not supported exceptions.
     *
     * @param ex      the HTTP request method not supported exception
     * @param request the HTTP servlet request
     * @return a response entity with method not allowed status and message
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.error("Method not supported", ex);
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("HTTP method " + ex.getMethod() + " is not supported for this endpoint")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }
    /**
     * Handles exceptions related to method argument type mismatches or missing parameters.
     *
     * @param ex      the exception (either type mismatch or missing parameter)
     * @param request the HTTP servlet request
     * @return a response entity with bad request status and descriptive message
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(Exception ex, HttpServletRequest request) {
        log.error("Invalid parameter", ex);
        String message = "Invalid parameter value";
        if (ex instanceof MissingServletRequestParameterException) {
            message = "Required parameter is missing: " + ((MissingServletRequestParameterException) ex).getParameterName();
        }
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles exceptions when the uploaded file exceeds the maximum allowed size.
     *
     * @param ex      the max upload size exceeded exception
     * @param request the HTTP servlet request
     * @return a response entity with payload too large status and message
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("File size exceeded", ex);
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .message("File size exceeds the maximum allowed limit")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.PAYLOAD_TOO_LARGE);
    }
    /**
     * Handles exceptions Method Argument Validation.
     *
     * @param ex      the data integrity violation exception
     * @param request the HTTP servlet request
     * @return a response entity with conflict status and message
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error", ex);
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errors(errors)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles exceptions caused by database constraint violations.
     *
     * @param ex      the data integrity violation exception
     * @param request the HTTP servlet request
     * @return a response entity with conflict status and message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Data integrity violation error", ex.getMostSpecificCause());
        String errorMessage = "Data integrity error occurred";
        String detailedMessage = ex.getMostSpecificCause().getMessage();
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .errors(Collections.singletonList(detailedMessage))
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles insufficient stock exceptions when requested quantity exceeds available stock.
     *
     * @param ex      the insufficient stock exception
     * @param request the HTTP servlet request
     * @return a response entity with bad request status and message
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStockException(
            InsufficientStockException ex, 
            HttpServletRequest request) {
        log.error("Insufficient stock error: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message("Insufficient stock")
                .errors(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    /**
     * Handles exceptions when an entity already exists.
     *
     * @param ex      the entity exists exception
     * @param request the HTTP servlet request
     * @return a response entity with conflict status and exception message
     */
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityExistsException(
            EntityExistsException ex,
            HttpServletRequest request) {
        log.error("Entity exists error: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message("Resource already exists")
                .errors(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    /**
     * Handles validation exceptions for constraint violations.
     *
     * @param ex      the constraint violation exception
     * @param request the HTTP servlet request
     * @return a response entity with bad request status and detailed validation error messages
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        log.error("Constraint violation", ex);

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String field = violation.getPropertyPath().toString();
                    String message = violation.getMessage();
                    return field + ": " + message;
                })
                .collect(Collectors.toList());

        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errors(errors)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles generic validation exceptions.
     *
     * @param ex      the validation exception
     * @param request the HTTP servlet request
     * @return a response entity with bad request status and exception message
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        log.error("Validation exception: ", ex);

        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errors(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    /**
     * Handles exceptions related to invalid data access API usage.
     *
     * @param ex      the invalid data access API usage exception
     * @param request the HTTP servlet request
     * @return a response entity with bad request status and message
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidDataAccessApiUsageException(
            InvalidDataAccessApiUsageException ex,
            HttpServletRequest request) {
        log.error("Invalid data access API usage: {}", ex.getMessage());
        
        String message = "Invalid data access operation";
        ex.getMostSpecificCause();
        String detailedMessage = ex.getMostSpecificCause().getMessage();
        
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .errors(Collections.singletonList(detailedMessage))
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions when no resource is found.
     *
     * @param ex      the no resource found exception
     * @param request the HTTP servlet request
     * @return a response entity with not found status and message
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Resource not found")
                .errors(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
