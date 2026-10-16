package com.luisartr.bakery.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(Long id, String name, BigDecimal salePrice, String unit,
                              BigDecimal yieldQuantity, List<RecipeItemResponse> recipe) {}