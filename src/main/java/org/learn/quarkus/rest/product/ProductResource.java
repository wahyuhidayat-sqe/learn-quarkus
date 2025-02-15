package org.learn.quarkus.rest.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.learn.quarkus.configs.Configs;
import org.learn.quarkus.models.secondary.Product;
import org.learn.quarkus.schema.ApiResponse;
import org.learn.quarkus.services.ProductService;

import java.util.List;

@Path("/product")
public class ProductResource {

    @Inject
    ProductService productService;

    @POST
    @SecurityRequirements(value = @SecurityRequirement(name = Configs.SECURITY_DEFINITION_NAME))
    public ApiResponse<Boolean> createProduct(Product product) {
        productService.create(product);
        return ApiResponse.ok(true);
    }

    @GET
    @SecurityRequirements(value = @SecurityRequirement(name = Configs.SECURITY_DEFINITION_NAME))
    public ApiResponse<List<Product>> getProducts() {
        return ApiResponse.ok(productService.findAll());
    }
}
