package com.hoang.microservices.order.service;

import com.hoang.microservices.order.client.InventoryClient;
import com.hoang.microservices.order.dto.OrderRequest;
import com.hoang.microservices.order.model.Order;
import com.hoang.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest) {
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        //map OrderRequest to Order Entity
        if (isProductInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setSkuCode(orderRequest.skuCode());
            order.setPrice(orderRequest.price());
            order.setQuantity(orderRequest.quantity());
            //save to DB
            orderRepository.save(order);
        }
        else {
            throw new RuntimeException("product with sku code " + orderRequest.skuCode() + " is not in stock");
        }
    }
}
