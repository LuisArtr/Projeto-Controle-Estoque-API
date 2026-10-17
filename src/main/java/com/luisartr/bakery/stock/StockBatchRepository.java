package com.luisartr.bakery.stock;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {

    Page<StockBatch> findByIngredientId(Long ingredientId, Pageable pageable);

    @Query("select coalesce(sum(b.currentQuantity), 0) from StockBatch b where b.ingredientId = :ingredientId")
    BigDecimal totalByIngredient(@Param("ingredientId") Long ingredientId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b from StockBatch b
            where b.ingredientId = :ingredientId
              and b.currentQuantity > 0
              and b.expirationDate >= :today
            order by b.expirationDate asc, b.id asc
            """)
    List<StockBatch> findUsableForUpdate(@Param("ingredientId") Long ingredientId,
                                         @Param("today") LocalDate today);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from StockBatch b where b.id = :id")
    java.util.Optional<StockBatch> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select b from StockBatch b
            where b.currentQuantity > 0
              and b.expirationDate between :from and :to
            order by b.expirationDate asc
            """)
    List<StockBatch> findExpiringBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
            select b from StockBatch b
            where b.currentQuantity > 0
              and b.expirationDate < :today
              and b.expiredProcessed = false
            """)
    List<StockBatch> findExpiredUnprocessed(@Param("today") LocalDate today);
}