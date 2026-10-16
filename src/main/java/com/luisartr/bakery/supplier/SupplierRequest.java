package com.luisartr.bakery.supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 160) String contact
) {}