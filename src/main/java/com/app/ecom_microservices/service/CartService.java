package com.app.ecom_microservices.service;

import com.app.ecom_microservices.dto.CartItemRequest;
import com.app.ecom_microservices.model.CartItem;
import com.app.ecom_microservices.model.Product;
import com.app.ecom_microservices.model.User;
import com.app.ecom_microservices.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository repository;
    private final ProductService productService;
    private final UserService userService;

    public boolean addToCart(String userId, CartItemRequest request) {
        var user = userService.fetchUser(Long.parseLong(userId));
        var product = productService.findProductById(request.getProductId());
        var price = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        var existingCartItem = getOptionCartItem(user, product);

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

    public boolean deleteItemFromCart(String userId, Long productId) {
        var user = userService.fetchUser(Long.parseLong(userId));
        var product = productService.findProductById(productId);
        return repository.deleteByUserAndProduct(user, product);
    }

    private Optional<CartItem> getOptionCartItem(User user, Product product) {
        return repository.findByUserAndProduct(user, product);
    }

    private CartItem getCartItem(String userId, Long productId) {
        var user = userService.fetchUser(Long.parseLong(userId));
        var product = productService.findProductById(productId);
        return getCartItem(user, product);
    }

    private CartItem getCartItem(User user, Product product) {
        return getOptionCartItem(user, product)
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));
    }
}
