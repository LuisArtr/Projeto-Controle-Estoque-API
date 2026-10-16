package com.luisartr.bakery.stock;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record StockEntryRequest(
        @NotNull Long ingredientId,
        Long supplierId,
        @NotNull @Positive BigDecimal quantity,
        @NotNull @Positive BigDecimal unitCost,
        @NotNull @FutureOrPresent LocalDate expirationDate
) {}