package com.luisartr.bakery.production;

import com.luisartr.bakery.common.BadRequestException;
import com.luisartr.bakery.common.ConflictException;
import com.luisartr.bakery.common.NotFoundException;
import com.luisartr.bakery.ingredient.Ingredient;
import com.luisartr.bakery.ingredient.IngredientRepository;
import com.luisartr.bakery.product.Product;
import com.luisartr.bakery.product.ProductRepository;
import com.luisartr.bakery.product.RecipeItem;
import com.luisartr.bakery.product.RecipeItemRepository;
import com.luisartr.bakery.stock.StockBatchRepository;
import com.luisartr.bakery.stock.StockMovement;
import com.luisartr.bakery.stock.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductionService {

    private final ProductRepository productRepository;
    private final RecipeItemRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final StockBatchRepository batchRepository;
    private final StockMovementRepository movementRepository;
    private final ProductionOrderRepository orderRepository;

    public ProductionService(ProductRepository productRepository,
                             RecipeItemRepository recipeRepository,
                             IngredientRepository ingredientRepository,
                             StockBatchRepository batchRepository,
                             StockMovementRepository movementRepository,
                             ProductionOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ProductionResponse produce(ProductionRequest req) {
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new NotFoundException("Produto não encontrado: " + req.productId()));

        List<RecipeItem> recipe = new ArrayList<>(recipeRepository.findByProductId(product.getId()));
        if (recipe.isEmpty()) {
            throw new BadRequestException("O produto não tem ficha técnica cadastrada.");
        }
        // ordem fixa de insumos evita deadlock entre produções simultâneas
        recipe.sort(Comparator.comparing(RecipeItem::getIngredientId));

        Map<Long, Ingredient> ingredients = ingredientRepository
                .findAllById(recipe.stream().map(RecipeItem::getIngredientId).toList())
                .stream().collect(Collectors.toMap(Ingredient::getId, Function.identity()));

        LocalDate today = LocalDate.now();
        Map<Long, FefoAllocator.Result> plan = new LinkedHashMap<>();
        List<String> missing = new ArrayList<>();

        // 1) planejar: trava os lotes e calcula o consumo, sem alterar nada ainda
        for (RecipeItem item : recipe) {
            BigDecimal needed = item.getQuantity().multiply(req.batches()).setScale(3, RoundingMode.HALF_UP);
            var usable = batchRepository.findUsableForUpdate(item.getIngredientId(), today);
            FefoAllocator.Result result = FefoAllocator.allocate(usable, needed);
            plan.put(item.getIngredientId(), result);

            if (result.shortfall().signum() > 0) {
                Ingredient ing = ingredients.get(item.getIngredientId());
                missing.add(ing.getName() + ": faltam " + result.shortfall().stripTrailingZeros().toPlainString()
                        + " " + ing.getBaseUnit());
            }
        }

        if (!missing.isEmpty()) {
            throw new ConflictException("Estoque insuficiente: " + String.join("; ", missing));
        }

        // 2) custo total
        BigDecimal totalCost = plan.values().stream()
                .flatMap(r -> r.allocations().stream())
                .map(a -> a.quantity().multiply(a.batch().getUnitCost()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(4, RoundingMode.HALF_UP);

        BigDecimal quantityProduced = product.getYieldQuantity().multiply(req.batches())
                .setScale(3, RoundingMode.HALF_UP);

        ProductionOrder order = orderRepository.save(
                new ProductionOrder(product.getId(), quantityProduced, req.batches(), totalCost));

        // 3) aplicar: baixa dos lotes e movimentações
        List<ConsumptionResponse> consumption = new ArrayList<>();
        plan.forEach((ingredientId, result) -> {
            Ingredient ing = ingredients.get(ingredientId);
            for (FefoAllocator.Allocation a : result.allocations()) {
                a.batch().consume(a.quantity());
                movementRepository.save(new StockMovement(ingredientId, a.batch().getId(),
                        "PRODUCAO", a.quantity(), "Produção #" + order.getId()));
                consumption.add(new ConsumptionResponse(ingredientId, ing.getName(),
                        a.batch().getId(), a.quantity(), a.batch().getUnitCost()));
            }
        });

        BigDecimal unitCost = totalCost.divide(quantityProduced, 4, RoundingMode.HALF_UP);

        return new ProductionResponse(order.getId(), product.getId(), product.getName(),
                req.batches(), quantityProduced, totalCost, unitCost, consumption);
    }

}