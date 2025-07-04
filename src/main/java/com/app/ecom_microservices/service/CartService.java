package com.app.ecom_microservices.service;

import com.app.ecom_microservices.dto.CartItemRequest;
import com.app.ecom_microservices.model.CartItem;
import com.app.ecom_microservices.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository repository;
    private final ProductService productService;
    private final UserService userService;

    public boolean addToCart(String userId, CartItemRequest request) {
        var user = userService.fetchUser(Long.parseLong(userId));
        var product = productService.findProductById(request.getProductId());
        var price = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        var existingCartItem = repository.findByUserAndProduct(user, product);

        var cartItem = existingCartItem
                .map(c -> {
                    c.setQuantity(c.getQuantity() + request.getQuantity());
                    return c;
                })
                .orElse(
                        new CartItem()
                                .withPrice(price)
                                .withQuantity(request.getQuantity())
                                .withUser(user)
                                .withProduct(product)
                );

        if (product.getStockQuantity() < cartItem.getQuantity()) {
            return false;
        }

        repository.save(cartItem);

        return true;
    }
}
