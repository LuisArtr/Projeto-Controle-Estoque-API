package com.luisartr.bakery.report;

import java.math.BigDecimal;

public record QuantityRow(Long ingredientId, String ingredientName, BigDecimal total) {}