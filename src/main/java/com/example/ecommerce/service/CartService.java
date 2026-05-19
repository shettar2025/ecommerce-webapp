package com.example.ecommerce.service;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {
  private Map<Integer, CartItem> cartItems = new HashMap<>();

  public void addProduct(Product product) {
    if (cartItems.containsKey(product.getId())) {
      CartItem item = cartItems.get(product.getId());
      item.setQuantity(item.getQuantity() + 1);
    } else {
      cartItems.put(product.getId(), new CartItem(product));
    }
  }

  public void removeProduct(int productId) {
    cartItems.remove(productId);
  }

  public Iterable<CartItem> getCartItems() {
    return cartItems.values();
  }

  public double getTotal() {
    return cartItems.values().stream()
            .mapToDouble(CartItem::getTotalPrice)
            .sum();
  }
}