package com.luisartr.bakery.product;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recipe_items")
public class RecipeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "ingredient_id", nullable = false)
    private Long ingredientId;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    protected RecipeItem() {}

    public RecipeItem(Long productId, Long ingredientId, BigDecimal quantity) {
        this.productId = productId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
    }

    public Long getIngredientId() { return ingredientId; }
    public BigDecimal getQuantity() { return quantity; }
}