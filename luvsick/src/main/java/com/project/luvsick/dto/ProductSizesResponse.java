package com.project.luvsick.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Data
@Getter
@Setter
public class ProductSizesResponse {
    private UUID id;
    private String size;
    private int quantity;
}
