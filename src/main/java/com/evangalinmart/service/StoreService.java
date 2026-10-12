package com.evangalinmart.service;

import com.evangalinmart.model.CustomerOrder;
import com.evangalinmart.model.OrderItem;
import com.evangalinmart.model.Product;
import com.evangalinmart.repository.CustomerOrderRepository;
import com.evangalinmart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class StoreService {
    private final ProductRepository products;
    private final CustomerOrderRepository orders;

    public StoreService(ProductRepository products, CustomerOrderRepository orders) {
        this.products = products;
        this.orders = orders;
    }

    public List<Product> search(String query) {
        return query == null || query.isBlank() ? products.findAll() : products.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(query.trim(), query.trim());
    }

    public BigDecimal total(Map<Long, Integer> cart) {
        return cart.entrySet().stream().map(entry -> products.findById(entry.getKey())
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(entry.getValue()))).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public CustomerOrder placeOrder(String name, String email, String address, Map<Long, Integer> cart) {
        CustomerOrder order = new CustomerOrder(name, email, address, total(cart));
        cart.forEach((id, quantity) -> products.findById(id).ifPresent(p -> order.addItem(new OrderItem(p.getName(), quantity, p.getPrice()))));
        return orders.save(order);
    }
}
