package com.luisartr.bakery.product;

import com.luisartr.bakery.common.BadRequestException;
import com.luisartr.bakery.common.ConflictException;
import com.luisartr.bakery.common.NotFoundException;
import com.luisartr.bakery.ingredient.Ingredient;
import com.luisartr.bakery.ingredient.IngredientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RecipeItemRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public ProductService(ProductRepository productRepository,
                          RecipeItemRepository recipeRepository,
                          IngredientRepository ingredientRepository) {
        this.productRepository = productRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        if (productRepository.existsByNameIgnoreCase(req.name())) {
            throw new ConflictException("Já existe um produto com o nome: " + req.name());
        }
        Product saved = productRepository.save(
                new Product(req.name(), req.salePrice(), req.unit(), req.yieldQuantity()));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> list(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product product = getOrThrow(id);
        if (productRepository.existsByNameIgnoreCaseAndIdNot(req.name(), id)) {
            throw new ConflictException("Já existe um produto com o nome: " + req.name());
        }
        product.update(req.name(), req.salePrice(), req.unit(), req.yieldQuantity());
        return toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = getOrThrow(id);
        recipeRepository.deleteByProductId(id);
        productRepository.delete(product);
        productRepository.flush();
    }

    @Transactional
    public ProductResponse replaceRecipe(Long productId, RecipeRequest req) {
        Product product = getOrThrow(productId);

        Set<Long> ids = new HashSet<>();
        for (RecipeItemRequest item : req.items()) {
            if (!ids.add(item.ingredientId())) {
                throw new BadRequestException("Insumo repetido na receita: " + item.ingredientId());
            }
        }

        List<Ingredient> found = ingredientRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            Set<Long> missing = new HashSet<>(ids);
            found.forEach(i -> missing.remove(i.getId()));
            throw new NotFoundException("Insumo(s) não encontrado(s): " + missing);
        }

        recipeRepository.deleteByProductId(productId);
        recipeRepository.saveAll(req.items().stream()
                .map(i -> new RecipeItem(productId, i.ingredientId(), i.quantity()))
                .toList());

        return toResponse(product);
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado: " + id));
    }

    private ProductResponse toResponse(Product p) {
        List<RecipeItem> items = recipeRepository.findByProductId(p.getId());
        Map<Long, Ingredient> ingredients = ingredientRepository
                .findAllById(items.stream().map(RecipeItem::getIngredientId).toList())
                .stream().collect(Collectors.toMap(Ingredient::getId, Function.identity()));

        List<RecipeItemResponse> recipe = items.stream()
                .map(i -> {
                    Ingredient ing = ingredients.get(i.getIngredientId());
                    return new RecipeItemResponse(ing.getId(), ing.getName(),
                            ing.getBaseUnit(), i.getQuantity());
                })
                .toList();

        return new ProductResponse(p.getId(), p.getName(), p.getSalePrice(),
                p.getUnit(), p.getYieldQuantity(), recipe);
    }
}