package com.example.BackendSpringAPI.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be input between 3 and 200 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equals to 0")
    @Max(value = 10000000, message = "Price must be less than or equal to 10,000,000")
    private Float price;
    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;
}
