package com.luisartr.bakery.production;

import java.math.BigDecimal;

public record ConsumptionResponse(Long ingredientId, String ingredientName,
                                  Long batchId, BigDecimal quantity, BigDecimal unitCost) {}