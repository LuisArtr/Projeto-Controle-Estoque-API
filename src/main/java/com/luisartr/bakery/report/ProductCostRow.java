package com.luisartr.bakery.report;

import java.math.BigDecimal;

public record ProductCostRow(Long productId, String productName, Long productions,
                             BigDecimal quantityProduced, BigDecimal totalCost) {}