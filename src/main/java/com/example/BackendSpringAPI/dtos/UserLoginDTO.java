package com.example.BackendSpringAPI.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
