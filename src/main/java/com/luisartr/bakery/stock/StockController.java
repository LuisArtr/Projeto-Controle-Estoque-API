package com.luisartr.bakery.stock;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService service;

    public StockController(StockService service) {
        this.service = service;
    }

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchResponse registerEntry(@Valid @RequestBody StockEntryRequest req) {
        return service.registerEntry(req);
    }

    @GetMapping("/batches")
    public Page<BatchResponse> listBatches(@RequestParam(required = false) Long ingredientId,
                                           Pageable pageable) {
        return service.listBatches(ingredientId, pageable);
    }
    @PostMapping("/losses")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchResponse registerLoss(@Valid @RequestBody LossRequest req) {
        return service.registerLoss(req);
    }

    @GetMapping("/alerts")
    public AlertsResponse alerts(@RequestParam(defaultValue = "7") int days) {
        return service.alerts(days);
    }

    @GetMapping("/balance/{ingredientId}")
    public BalanceResponse balance(@PathVariable Long ingredientId) {
        return service.balance(ingredientId);
    }
}