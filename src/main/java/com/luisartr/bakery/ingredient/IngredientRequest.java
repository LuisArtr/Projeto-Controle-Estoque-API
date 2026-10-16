package com.luisartr.bakery.ingredient;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record IngredientRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 10) String baseUnit,
        @NotNull @PositiveOrZero BigDecimal minimumStock
) {}