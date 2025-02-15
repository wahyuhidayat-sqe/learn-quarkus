package org.learn.quarkus.repositories.product;

import org.learn.quarkus.models.secondary.Product;

import java.util.List;

public interface ProductRepository {
    Product create(Product product);

    List<Product> findAll();
}
