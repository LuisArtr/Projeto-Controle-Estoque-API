package com.luisartr.bakery.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RecipeRequest(@NotEmpty @Valid List<RecipeItemRequest> items) {}