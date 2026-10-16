package com.luisartr.bakery.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {

    Page<StockBatch> findByIngredientId(Long ingredientId, Pageable pageable);

    @Query("select coalesce(sum(b.currentQuantity), 0) from StockBatch b where b.ingredientId = :ingredientId")
    BigDecimal totalByIngredient(@Param("ingredientId") Long ingredientId);
}