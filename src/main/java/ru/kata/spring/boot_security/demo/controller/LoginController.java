package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @GetMapping
    public String showLoginPage() {
        logger.info("showLoginPage");
        return "login";
    }
}
