package com.luisartr.bakery.stock;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BatchResponse(Long id, Long ingredientId, Long supplierId,
                            BigDecimal currentQuantity, BigDecimal unitCost,
                            LocalDate expirationDate) {

    public static BatchResponse from(StockBatch b) {
        return new BatchResponse(b.getId(), b.getIngredientId(), b.getSupplierId(),
                b.getCurrentQuantity(), b.getUnitCost(), b.getExpirationDate());
    }
}