package ru.kata.spring.boot_security.demo.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginRestController {
    @GetMapping
    public String showLoginPage() {
        return "login";
    }
}
