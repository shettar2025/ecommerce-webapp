package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/ecommerce")
  public String ecommercePage() {
    // Return the name of a Thymeleaf template (e.g., "index.html")
    return "index";
  }
}
