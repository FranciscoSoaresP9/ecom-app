package com.app.ecom_microservices.repository;

import com.app.ecom_microservices.model.CartItem;
import com.app.ecom_microservices.model.Product;
import com.app.ecom_microservices.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    boolean deleteByUserAndProduct(User user, Product product);
}
