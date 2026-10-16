package com.luisartr.bakery.supplier;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 160)
    private String contact;

    protected Supplier() {}

    public Supplier(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }

    public void update(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }
}