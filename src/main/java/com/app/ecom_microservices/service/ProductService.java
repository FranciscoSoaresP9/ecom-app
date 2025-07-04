package com.app.ecom_microservices.service;

import com.app.ecom_microservices.dto.ProductRequest;
import com.app.ecom_microservices.dto.ProductResponse;
import com.app.ecom_microservices.model.Product;
import com.app.ecom_microservices.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Set<ProductResponse> getAll() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toSet());
    }

    public Set<ProductResponse> search(@RequestParam String keyword) {
        return repository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toSet());
    }


    public ProductResponse getById(Long id) {
        return mapToProductResponse(findProductById(id));
    }

    public ProductResponse create(ProductRequest request) {
        var product = new Product();
        return update(product, request);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        var product = findProductById(id);
        return update(product, request);
    }

    public void delete(Long id) {
        var product = findProductById(id);
        product.setActive(false);
        repository.save(product);
    }

    private ProductResponse update(Product product, ProductRequest request) {
        updateProductFromRequest(product, request);
        return mapToProductResponse(repository.save(product));
    }

    public Product findProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse()
                .withId(product.getId().toString())
                .withName(product.getName())
                .withDescription(product.getDescription())
                .withPrice(product.getPrice())
                .withStockQuantity(product.getStockQuantity())
                .withCategory(product.getCategory())
                .withImageUrl(product.getImageUrl())
                .withActive(product.getActive());
    }
}
