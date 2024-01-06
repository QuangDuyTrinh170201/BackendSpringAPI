package com.example.BackendSpringAPI.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
