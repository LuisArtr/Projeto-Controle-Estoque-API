package com.luisartr.bakery.stock;

import java.math.BigDecimal;

public record BalanceResponse(Long ingredientId, String ingredientName, String baseUnit,
                              BigDecimal balance, BigDecimal minimumStock, boolean belowMinimum) {}