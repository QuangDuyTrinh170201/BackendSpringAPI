package com.example.BackendSpringAPI.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDTO {
    @JsonProperty("product_id")
    @Min(value = 1, message = "Product id must be > 0")
    private Long productId;

    @Size(min = 5, max = 200, message = "Image's name")
    @JsonProperty("product_id")
    private String imageUrl;
}
