package com.luisartr.bakery.user;

import jakarta.validation.constraints.*;

public record UserRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Pattern(regexp = "ADMIN|PADEIRO|ESTOQUISTA",
                message = "deve ser ADMIN, PADEIRO ou ESTOQUISTA") String role
) {}