package com.luisartr.bakery.production;

import com.luisartr.bakery.stock.StockBatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FefoAllocator {

    public record Allocation(StockBatch batch, BigDecimal quantity) {}

    public record Result(List<Allocation> allocations, BigDecimal shortfall) {}

    private FefoAllocator() {}

    /** Distribui a quantidade necessária pelos lotes, do que vence primeiro ao que vence depois. */
    public static Result allocate(List<StockBatch> batches, BigDecimal needed) {
        List<StockBatch> sorted = new ArrayList<>(batches);
        sorted.sort(Comparator.comparing(StockBatch::getExpirationDate)
                .thenComparing(StockBatch::getId, Comparator.nullsLast(Comparator.<Long>naturalOrder())));

        List<Allocation> allocations = new ArrayList<>();
        BigDecimal remaining = needed;

        for (StockBatch batch : sorted) {
            if (remaining.signum() <= 0) break;
            BigDecimal available = batch.getCurrentQuantity();
            if (available.signum() <= 0) continue;

            BigDecimal take = available.min(remaining);
            allocations.add(new Allocation(batch, take));
            remaining = remaining.subtract(take);
        }
        return new Result(allocations, remaining.max(BigDecimal.ZERO));
    }
}