package com.luisartr.bakery.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record RecipeItemRequest(
        @NotNull Long ingredientId,
        @NotNull @Positive BigDecimal quantity
) {}