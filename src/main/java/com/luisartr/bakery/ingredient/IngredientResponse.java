package com.luisartr.bakery.ingredient;

import java.math.BigDecimal;

public record IngredientResponse(Long id, String name, String baseUnit, BigDecimal minimumStock) {

    public static IngredientResponse from(Ingredient i) {
        return new IngredientResponse(i.getId(), i.getName(), i.getBaseUnit(), i.getMinimumStock());
    }
}