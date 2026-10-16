package com.luisartr.bakery.product;

import java.math.BigDecimal;

public record RecipeItemResponse(Long ingredientId, String ingredientName,
                                 String unit, BigDecimal quantity) {}