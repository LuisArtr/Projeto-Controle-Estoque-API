package com.luisartr.bakery.supplier;

public record SupplierResponse(Long id, String name, String contact) {

    public static SupplierResponse from(Supplier s) {
        return new SupplierResponse(s.getId(), s.getName(), s.getContact());
    }
}