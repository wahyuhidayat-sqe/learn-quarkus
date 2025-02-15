package org.learn.quarkus.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.learn.quarkus.models.secondary.Product;
import org.learn.quarkus.repositories.product.ProductRepository;

import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;


    public Product create(Product product) {
        productRepository.create(product);
        return product;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
