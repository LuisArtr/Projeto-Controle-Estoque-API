package com.luisartr.bakery.stock;

import com.luisartr.bakery.report.QuantityRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("""
            select new com.luisartr.bakery.report.QuantityRow(i.id, i.name, sum(m.quantity))
            from StockMovement m join Ingredient i on i.id = m.ingredientId
            where m.type = :type and m.createdAt >= :from and m.createdAt < :to
            group by i.id, i.name
            order by sum(m.quantity) desc
            """)
    List<QuantityRow> totalsByType(@Param("type") String type,
                                   @Param("from") OffsetDateTime from,
                                   @Param("to") OffsetDateTime to);
}