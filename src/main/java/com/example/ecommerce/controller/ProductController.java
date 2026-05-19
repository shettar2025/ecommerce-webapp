package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

  @Autowired
  private ProductService productService;

  // List all products
  @GetMapping("/products")
  public String products(Model model) {
    model.addAttribute("products", productService.getAllProducts());
    return "products";
  }

  // Show product details by id
  @GetMapping("/product/{id}")
  public String productDetail(@PathVariable("id") int id, Model model) {
    Product product = productService.getProductById(id);
    if (product == null) {
      return "redirect:/products";
    }
    model.addAttribute("product", product);
    return "product";
  }
}
