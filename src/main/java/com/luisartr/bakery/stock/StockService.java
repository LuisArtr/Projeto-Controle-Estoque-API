package com.luisartr.bakery.stock;

import com.luisartr.bakery.common.ConflictException;
import com.luisartr.bakery.common.NotFoundException;
import com.luisartr.bakery.ingredient.Ingredient;
import com.luisartr.bakery.ingredient.IngredientRepository;
import com.luisartr.bakery.supplier.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import java.math.BigDecimal;

@Service
public class StockService {

    private final StockBatchRepository batchRepository;
    private final StockMovementRepository movementRepository;
    private final IngredientRepository ingredientRepository;
    private final SupplierRepository supplierRepository;

    public StockService(StockBatchRepository batchRepository,
                        StockMovementRepository movementRepository,
                        IngredientRepository ingredientRepository,
                        SupplierRepository supplierRepository) {
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
        this.ingredientRepository = ingredientRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public BatchResponse registerEntry(StockEntryRequest req) {
        if (!ingredientRepository.existsById(req.ingredientId())) {
            throw new NotFoundException("Insumo não encontrado: " + req.ingredientId());
        }
        if (req.supplierId() != null && !supplierRepository.existsById(req.supplierId())) {
            throw new NotFoundException("Fornecedor não encontrado: " + req.supplierId());
        }

        StockBatch batch = batchRepository.save(new StockBatch(
                req.ingredientId(), req.supplierId(), req.quantity(),
                req.unitCost(), req.expirationDate()));

        movementRepository.save(new StockMovement(
                req.ingredientId(), batch.getId(), "ENTRADA", req.quantity(), "Entrada de lote"));

        return BatchResponse.from(batch);
    }

    @Transactional(readOnly = true)
    public Page<BatchResponse> listBatches(Long ingredientId, Pageable pageable) {
        Page<StockBatch> page = (ingredientId == null)
                ? batchRepository.findAll(pageable)
                : batchRepository.findByIngredientId(ingredientId, pageable);
        return page.map(BatchResponse::from);
    }

    @Transactional(readOnly = true)
    public BalanceResponse balance(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new NotFoundException("Insumo não encontrado: " + ingredientId));
        BigDecimal balance = batchRepository.totalByIngredient(ingredientId);
        return new BalanceResponse(ingredient.getId(), ingredient.getName(), ingredient.getBaseUnit(),
                balance, ingredient.getMinimumStock(),
                balance.compareTo(ingredient.getMinimumStock()) < 0);
    }
    @Transactional
    public BatchResponse registerLoss(LossRequest req) {
        StockBatch batch = batchRepository.findByIdForUpdate(req.batchId())
                .orElseThrow(() -> new NotFoundException("Lote não encontrado: " + req.batchId()));

        if (req.quantity().compareTo(batch.getCurrentQuantity()) > 0) {
            throw new ConflictException("Perda maior que o saldo do lote ("
                    + batch.getCurrentQuantity().stripTrailingZeros().toPlainString() + ").");
        }

        batch.consume(req.quantity());
        movementRepository.save(new StockMovement(batch.getIngredientId(), batch.getId(),
                "PERDA", req.quantity(), req.reason()));
        return BatchResponse.from(batch);
    }

    @Transactional(readOnly = true)
    public AlertsResponse alerts(int days) {
        List<BalanceResponse> below = ingredientRepository.findAll().stream()
                .map(i -> balance(i.getId()))
                .filter(BalanceResponse::belowMinimum)
                .toList();

        LocalDate today = LocalDate.now();
        List<BatchResponse> expiring = batchRepository
                .findExpiringBetween(today, today.plusDays(days)).stream()
                .map(BatchResponse::from)
                .toList();

        return new AlertsResponse(below, expiring);
    }

    @Transactional
    public int processExpiredBatches() {
        List<StockBatch> expired = batchRepository.findExpiredUnprocessed(LocalDate.now());
        for (StockBatch batch : expired) {
            movementRepository.save(new StockMovement(batch.getIngredientId(), batch.getId(),
                    "PERDA", batch.getCurrentQuantity(), "Lote vencido"));
            batch.consume(batch.getCurrentQuantity());
            batch.markExpiredProcessed();
        }
        return expired.size();
    }
}
