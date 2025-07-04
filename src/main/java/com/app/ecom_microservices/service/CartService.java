package com.app.ecom_microservices.service;

import com.app.ecom_microservices.dto.CartItemRequest;
import com.app.ecom_microservices.dto.CartItemResponse;
import com.app.ecom_microservices.dto.ProductResponse;
import com.app.ecom_microservices.dto.UserResponse;
import com.app.ecom_microservices.model.CartItem;
import com.app.ecom_microservices.model.Product;
import com.app.ecom_microservices.model.User;
import com.app.ecom_microservices.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<CartItemResponse> getCart(String userId) {
        var user = userService.fetchUser(Long.parseLong(userId));
        return repository.findByUser(user).stream()
                .map(this::mapCartItemToResponse)
                .collect(Collectors.toList());
    }

    private Optional<CartItem> getOptionCartItem(User user, Product product) {
        return repository.findByUserAndProduct(user, product);
    }

    private CartItemResponse mapCartItemToResponse(CartItem cartItem) {
        return new CartItemResponse()
                .withProduct(
                        new ProductResponse()
                                .withId(cartItem.getProduct().getId().toString())
                                .withName(cartItem.getProduct().getName())
                                .withPrice(cartItem.getProduct().getPrice())
                )
                .withUser(
                        new UserResponse()
                                .withId(cartItem.getUser().getId().toString())
                                .withFirstName(cartItem.getUser().getFirstName())
                                .withLastName(cartItem.getUser().getLastName())
                )
                .withQuantity(cartItem.getQuantity())
                .withPrice(cartItem.getProduct().getPrice());
    }

}
