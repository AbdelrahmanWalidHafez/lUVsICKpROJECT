package com.project.luvsick.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private UUID id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Name can only contain letters, spaces, dots, hyphens and apostrophes")
    private String name;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "City can only contain letters, spaces, dots, hyphens and apostrophes")
    private String city;

    @NotBlank(message = "Street is required")
    @Size(min = 3, max = 100, message = "Street must be between 3 and 100 characters")
    @Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "Street can only contain letters, numbers, spaces, dots, hyphens and apostrophes")
    private String street;

    @NotBlank(message = "Building number is required")
    @Size(min = 1, max = 10, message = "Building number must be between 1 and 10 characters")
    @Pattern(regexp = "^[0-9]+[A-Za-z]?$", message = "Building number must start with numbers and can end with a single letter")
    private String buildingNumber;

    @NotBlank(message = "Flat number is required")
    @Size(min = 1, max = 10, message = "Flat number must be between 1 and 10 characters")
    @Pattern(regexp = "^[0-9]+[A-Za-z]?$", message = "Flat number must start with numbers and can end with a single letter")
    private String flatNumber;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(01)[0-2,5]{1}[0-9]{8}$",
        message = "Invalid Egyptian phone number. Must start with 01 followed by 0,1,2, or 5 and then 8 digits"
    )
    private String phoneNumber;
}
