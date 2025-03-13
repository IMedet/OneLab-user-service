package kz.medet.userservice.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {

    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @NotNull(message = "Password cannot be empty")
    private String password;
}
