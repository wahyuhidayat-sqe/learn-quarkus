package org.learn.quarkus.repositories.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import org.learn.quarkus.models.secondary.Product;

import java.util.List;

@ApplicationScoped
public class ProductRepositoryImpl implements ProductRepository {

    @Inject
    @PersistenceUnit(name = "secondary", unitName = "secondary")
    EntityManager em;

    @Override
    @Transactional
    public Product create(Product product) {
        em.persist(product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }
}
