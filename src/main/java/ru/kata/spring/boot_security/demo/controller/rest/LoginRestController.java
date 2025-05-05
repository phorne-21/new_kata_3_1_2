package ru.kata.spring.boot_security.demo.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginRestController {

    @GetMapping("/login")
    public ResponseEntity<?> showLoginPage() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Spring Security автоматически обработает выход
        return ResponseEntity.ok().build();
    }
}
