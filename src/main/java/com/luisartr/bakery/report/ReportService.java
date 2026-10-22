package com.luisartr.bakery.report;

import com.luisartr.bakery.common.BadRequestException;
import com.luisartr.bakery.production.ProductionOrderRepository;
import com.luisartr.bakery.stock.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReportService {

    private final StockMovementRepository movements;
    private final ProductionOrderRepository orders;

    public ReportService(StockMovementRepository movements, ProductionOrderRepository orders) {
        this.movements = movements;
        this.orders = orders;
    }

    @Transactional(readOnly = true)
    public List<QuantityRow> losses(LocalDate from, LocalDate to) {
        return movements.totalsByType("PERDA", start(from, to), end(to));
    }

    @Transactional(readOnly = true)
    public List<QuantityRow> consumption(LocalDate from, LocalDate to) {
        return movements.totalsByType("PRODUCAO", start(from, to), end(to));
    }

    @Transactional(readOnly = true)
    public List<ProductCostResponse> productCosts(LocalDate from, LocalDate to) {
        return orders.costsByProduct(start(from, to), end(to)).stream()
                .map(r -> new ProductCostResponse(r.productId(), r.productName(), r.productions(),
                        r.quantityProduced(), r.totalCost(),
                        r.quantityProduced().signum() == 0 ? BigDecimal.ZERO
                                : r.totalCost().divide(r.quantityProduced(), 4, RoundingMode.HALF_UP)))
                .toList();
    }

    private OffsetDateTime start(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new BadRequestException("A data inicial não pode ser depois da final.");
        }
        return from.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private OffsetDateTime end(LocalDate to) {
        return to.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }
}