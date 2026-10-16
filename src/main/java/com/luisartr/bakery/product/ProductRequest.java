package com.luisartr.bakery.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull @PositiveOrZero BigDecimal salePrice,
        @NotBlank @Size(max = 10) String unit,
        @NotNull @Positive BigDecimal yieldQuantity
) {}