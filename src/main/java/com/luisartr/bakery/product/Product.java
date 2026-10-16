package com.luisartr.bakery.product;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(nullable = false, length = 10)
    private String unit;

    @Column(name = "yield_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal yieldQuantity;

    protected Product() {}

    public Product(String name, BigDecimal salePrice, String unit, BigDecimal yieldQuantity) {
        this.name = name;
        this.salePrice = salePrice;
        this.unit = unit;
        this.yieldQuantity = yieldQuantity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getSalePrice() { return salePrice; }
    public String getUnit() { return unit; }
    public BigDecimal getYieldQuantity() { return yieldQuantity; }

    public void update(String name, BigDecimal salePrice, String unit, BigDecimal yieldQuantity) {
        this.name = name;
        this.salePrice = salePrice;
        this.unit = unit;
        this.yieldQuantity = yieldQuantity;
    }
}