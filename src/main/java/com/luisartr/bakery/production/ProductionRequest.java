package com.luisartr.bakery.production;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductionRequest(
        @NotNull Long productId,
        @NotNull @Positive BigDecimal batches
) {}