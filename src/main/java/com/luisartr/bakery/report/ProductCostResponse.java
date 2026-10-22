package com.luisartr.bakery.report;

import java.math.BigDecimal;

public record ProductCostResponse(Long productId, String productName, Long productions,
                                  BigDecimal quantityProduced, BigDecimal totalCost,
                                  BigDecimal averageUnitCost) {}