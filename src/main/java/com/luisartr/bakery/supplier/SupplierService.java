package com.luisartr.bakery.supplier;

import com.luisartr.bakery.common.ConflictException;
import com.luisartr.bakery.common.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierService {

    private final SupplierRepository repository;

    public SupplierService(SupplierRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SupplierResponse create(SupplierRequest req) {
        if (repository.existsByNameIgnoreCase(req.name())) {
            throw new ConflictException("Já existe um fornecedor com o nome: " + req.name());
        }
        return SupplierResponse.from(repository.save(new Supplier(req.name(), req.contact())));
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(SupplierResponse::from);
    }

    @Transactional(readOnly = true)
    public SupplierResponse findById(Long id) {
        return SupplierResponse.from(getOrThrow(id));
    }

    @Transactional
    public SupplierResponse update(Long id, SupplierRequest req) {
        Supplier supplier = getOrThrow(id);
        supplier.update(req.name(), req.contact());
        return SupplierResponse.from(supplier);
    }
    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
        repository.flush();
    }
    private Supplier getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado: " + id));
    }
}   