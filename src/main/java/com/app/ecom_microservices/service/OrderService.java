package com.app.ecom_microservices.service;

import com.app.ecom_microservices.dto.OrderItemDTO;
import com.app.ecom_microservices.dto.OrderResponse;
import com.app.ecom_microservices.model.CartItem;
import com.app.ecom_microservices.model.Order;
import com.app.ecom_microservices.model.OrderItem;
import com.app.ecom_microservices.model.OrderStatus;
import com.app.ecom_microservices.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;

    public OrderResponse createOrder(String userId) {
        var items = cartService.getCart(userId);
        var user = userService.fetchUser(Long.parseLong(userId));

        if (items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        var orderItems = items.stream()
                .map(this::mapCartItemToOrderItem)
                .collect(Collectors.toList());

        var order = new Order()
                .withUser(user)
                .withStatus(OrderStatus.CONFIRMED)
                .withTotalAmount(getTotalPrice(items))
                .withItems(orderItems);

        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        cartService.clearCart(Long.parseLong(userId));
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    private BigDecimal getTotalPrice(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItem mapCartItemToOrderItem(CartItem item) {
        return new OrderItem()
                .withProduct(item.getProduct())
                .withQuantity(item.getQuantity())
                .withPrice(item.getPrice());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse()
                .withOrderId(order.getId())
                .withItems(order.getItems().stream().map(this::mapOrderItemToDTO).collect(Collectors.toList()))
                .withStatus(order.getStatus())
                .withTotalAmount(order.getTotalAmount())
                .withCreatedAt(order.getCreatedAt());
    }

    private OrderItemDTO mapOrderItemToDTO(OrderItem orderItem) {
        return new OrderItemDTO()
                .withId(orderItem.getId())
                .withPrice(orderItem.getPrice())
                .withQuantity(orderItem.getQuantity())
                .withProductId(orderItem.getProduct().getId())
                .withSubTotal(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
    }

}
