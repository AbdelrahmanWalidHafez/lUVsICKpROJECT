package com.project.luvsick.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Builder
@Data
public class UserDTO {
    private UUID uuid;
    private String email;
    private String name;

}
