package com.luisartr.bakery.production;

import java.math.BigDecimal;
import java.util.List;

public record ProductionResponse(Long orderId, Long productId, String productName,
                                 BigDecimal batches, BigDecimal quantityProduced,
                                 BigDecimal totalCost, BigDecimal unitCost,
                                 List<ConsumptionResponse> consumption) {}