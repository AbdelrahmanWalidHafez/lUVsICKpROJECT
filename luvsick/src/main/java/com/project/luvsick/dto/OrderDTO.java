package com.project.luvsick.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
public class OrderDTO {
    @NotNull(message = "Customer information is required")
    @Valid
    private CustomerDTO customerDTO;
    @NotNull(message = "at least one product is required")
    private List<UUID>productUUIDS;
    @NotNull
    private Map<UUID,Integer>productSizesUUIDS;
}
