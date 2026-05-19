package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {

  @Autowired
  private CartService cartService;

  @Autowired
  private ProductService productService;

  // Initialize a session-scoped cart
  @ModelAttribute("cart")
  public CartService cart() {
    return new CartService();
  }

  // View cart items
  @GetMapping
  public String viewCart(@ModelAttribute("cart") CartService cart, Model model) {
    model.addAttribute("cartItems", cart.getCartItems());
    model.addAttribute("total", cart.getTotal());
    return "cart";
  }

  // Add a product to the cart
  @PostMapping("/add/{productId}")
  public String addToCart(@PathVariable("productId") int productId, @ModelAttribute("cart") CartService cart) {
    Product product = productService.getProductById(productId);
    if (product != null) {
      cart.addProduct(product);
    }
    return "redirect:/cart";
  }

  // Remove a product from the cart
  @PostMapping("/remove/{productId}")
  public String removeFromCart(@PathVariable("productId") int productId, @ModelAttribute("cart") CartService cart) {
    cart.removeProduct(productId);
    return "redirect:/cart";
  }
}
