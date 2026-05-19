package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {
  private List<Product> products = new ArrayList<>();

  public ProductService() {
    // Sample products
    products.add(new Product(1, "Smartphone", "Latest smartphone with amazing features", 699.99, "https://via.placeholder.com/150"));
    products.add(new Product(2, "Laptop", "Powerful laptop for professionals", 1299.99, "https://via.placeholder.com/150"));
    products.add(new Product(3, "Headphones", "Noise-cancelling headphones", 199.99, "https://via.placeholder.com/150"));
  }

  public List<Product> getAllProducts() {
    return products;
  }

  public Product getProductById(int id) {
    return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
  }
}
