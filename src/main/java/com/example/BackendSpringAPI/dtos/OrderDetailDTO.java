package com.example.BackendSpringAPI.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value=1, message = "Order id must be > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product id must be > 0")
    private Long productId;

    @Min(value = 1, message = "Product id must be >= 0")
    private Float price;

    @Min(value = 1, message = "Product id must be >= 1")
    @JsonProperty("number_of_products")
    private int numberOfProduct;

    @Min(value = 1, message = "Product id must be > 0")
    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
}
