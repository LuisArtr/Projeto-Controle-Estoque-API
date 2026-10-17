package com.luisartr.bakery.ingredient;

import com.luisartr.bakery.common.ConflictException;
import com.luisartr.bakery.common.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientService {

    private final IngredientRepository repository;

    public IngredientService(IngredientRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IngredientResponse create(IngredientRequest req) {
        if (repository.existsByNameIgnoreCase(req.name())) {
            throw new ConflictException("Já existe um insumo com o nome: " + req.name());
        }
        Ingredient saved = repository.save(new Ingredient(req.name(), req.baseUnit(), req.minimumStock()));
        return IngredientResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<IngredientResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(IngredientResponse::from);
    }

    @Transactional(readOnly = true)
    public IngredientResponse findById(Long id) {
        return IngredientResponse.from(getOrThrow(id));
    }

    @Transactional
    public IngredientResponse update(Long id, IngredientRequest req) {
        Ingredient ingredient = getOrThrow(id);
        ingredient.update(req.name(), req.baseUnit(), req.minimumStock());
        return IngredientResponse.from(ingredient);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
        repository.flush();
    }

    private Ingredient getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Insumo não encontrado: " + id));
    }

}