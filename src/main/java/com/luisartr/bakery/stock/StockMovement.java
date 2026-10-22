package com.luisartr.bakery.stock;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingredient_id", nullable = false)
    private Long ingredientId;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(length = 255)
    private String reason;

    protected StockMovement() {}

    public StockMovement(Long ingredientId, Long batchId, String type,
                         BigDecimal quantity, String reason) {
        this.ingredientId = ingredientId;
        this.batchId = batchId;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
    }
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
}