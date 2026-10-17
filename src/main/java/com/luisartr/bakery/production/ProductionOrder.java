package com.luisartr.bakery.production;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "production_orders")
public class ProductionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity_produced", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantityProduced;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal batches;

    @Column(name = "total_cost", nullable = false, precision = 14, scale = 4)
    private BigDecimal totalCost;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected ProductionOrder() {}

    public ProductionOrder(Long productId, BigDecimal quantityProduced,
                           BigDecimal batches, BigDecimal totalCost) {
        this.productId = productId;
        this.quantityProduced = quantityProduced;
        this.batches = batches;
        this.totalCost = totalCost;
    }

    public Long getId() { return id; }
}