package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    // Show the login page (GET request)
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // Thymeleaf template name
    }

    // Handle form submission (POST request)
    @PostMapping("/login")
    public String processLogin(String username, String password) {
        // TODO: Implement real authentication logic here
        // For now, let's just redirect to the home page
        return "redirect:/";
    }
}
