package com.luisartr.bakery.stock;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "stock_batches")
public class StockBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingredient_id", nullable = false)
    private Long ingredientId;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "current_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal currentQuantity;

    @Column(name = "unit_cost", nullable = false, precision = 12, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "entered_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime enteredAt;

    protected StockBatch() {}

    public StockBatch(Long ingredientId, Long supplierId, BigDecimal currentQuantity,
                      BigDecimal unitCost, LocalDate expirationDate) {
        this.ingredientId = ingredientId;
        this.supplierId = supplierId;
        this.currentQuantity = currentQuantity;
        this.unitCost = unitCost;
        this.expirationDate = expirationDate;
    }

    public Long getId() { return id; }
    public Long getIngredientId() { return ingredientId; }
    public Long getSupplierId() { return supplierId; }
    public BigDecimal getCurrentQuantity() { return currentQuantity; }
    public BigDecimal getUnitCost() { return unitCost; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public OffsetDateTime getEnteredAt() { return enteredAt; }
}