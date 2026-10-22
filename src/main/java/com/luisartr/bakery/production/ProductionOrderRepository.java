package com.luisartr.bakery.production;

import com.luisartr.bakery.report.ProductCostRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    @Query("""
            select new com.luisartr.bakery.report.ProductCostRow(
                p.id, p.name, count(o), sum(o.quantityProduced), sum(o.totalCost))
            from ProductionOrder o join com.luisartr.bakery.product.Product p on p.id = o.productId
            where o.createdAt >= :from and o.createdAt < :to
            group by p.id, p.name
            """)
    List<ProductCostRow> costsByProduct(@Param("from") OffsetDateTime from,
                                        @Param("to") OffsetDateTime to);
}