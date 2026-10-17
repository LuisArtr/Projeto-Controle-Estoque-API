package com.luisartr.bakery.stock;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record LossRequest(
        @NotNull Long batchId,
        @NotNull @Positive BigDecimal quantity,
        @NotBlank @Size(max = 255) String reason
) {}