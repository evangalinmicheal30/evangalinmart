package com.evangalinmart.config;

import com.evangalinmart.model.Product;
import com.evangalinmart.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner sampleProducts(ProductRepository products) {
        return args -> {
            if (products.count() != 0) return;
            products.save(new Product("Farm Fresh Tomatoes", "Fresh Vegetables", "Juicy, ripe tomatoes picked for everyday cooking.", new BigDecimal("48.00"), "https://images.unsplash.com/photo-1546094096-0df4bcaaa337?auto=format&fit=crop&w=900&q=85", true));
            products.save(new Product("Sweet Red Apples", "Fresh Fruits", "Crisp apples packed with a naturally sweet bite.", new BigDecimal("160.00"), "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=900&q=85", true));
            products.save(new Product("Garden Green Spinach", "Fresh Vegetables", "Tender leafy greens, ready for your favourite recipes.", new BigDecimal("35.00"), "https://images.unsplash.com/photo-1576045057995-568f588f82fb?auto=format&fit=crop&w=900&q=85", true));
            products.save(new Product("Fresh Farm Milk", "Dairy & Eggs", "Creamy fresh milk for breakfast, tea and more.", new BigDecimal("32.00"), "https://images.unsplash.com/photo-1563636619-e9143da7973b?auto=format&fit=crop&w=900&q=85", true));
            products.save(new Product("Golden Bananas", "Fresh Fruits", "Naturally sweet bananas, perfect for a quick snack.", new BigDecimal("55.00"), "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?auto=format&fit=crop&w=900&q=85", false));
            products.save(new Product("Whole Wheat Bread", "Bakery", "Soft, hearty bread baked for your daily table.", new BigDecimal("52.00"), "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=900&q=85", false));
            products.save(new Product("Basmati Rice", "Pantry Staples", "Long-grain aromatic rice for special meals and everyday favourites.", new BigDecimal("125.00"), "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=85", false));
            products.save(new Product("Farm Fresh Eggs", "Dairy & Eggs", "A tray of fresh eggs for wholesome meals.", new BigDecimal("78.00"), "https://images.unsplash.com/photo-1518569656558-1f25e69d93d7?auto=format&fit=crop&w=900&q=85", false));
        };
    }
}
