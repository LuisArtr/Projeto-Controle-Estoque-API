package com.luisartr.bakery.ingredient;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(name = "base_unit", nullable = false, length = 10)
    private String baseUnit;

    @Column(name = "minimum_stock", nullable = false, precision = 12, scale = 3)
    private BigDecimal minimumStock = BigDecimal.ZERO;

    protected Ingredient() {}

    public Ingredient(String name, String baseUnit, BigDecimal minimumStock) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.minimumStock = minimumStock;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBaseUnit() { return baseUnit; }
    public BigDecimal getMinimumStock() { return minimumStock; }

    public void update(String name, String baseUnit, BigDecimal minimumStock) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.minimumStock = minimumStock;
    }
}