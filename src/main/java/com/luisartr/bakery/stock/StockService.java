package com.luisartr.bakery.stock;

import com.luisartr.bakery.common.NotFoundException;
import com.luisartr.bakery.ingredient.Ingredient;
import com.luisartr.bakery.ingredient.IngredientRepository;
import com.luisartr.bakery.supplier.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
