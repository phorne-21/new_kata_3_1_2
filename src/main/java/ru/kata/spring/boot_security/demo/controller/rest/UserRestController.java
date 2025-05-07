package ru.kata.spring.boot_security.demo.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.DTO.UserReadDTO;
import ru.kata.spring.boot_security.demo.service.DTO.UserDTOService;

import java.util.logging.Logger;

@RestController
@RequestMapping("/user")
public class UserRestController {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final UserDTOService userService;

    public UserRestController(UserDTOService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserReadDTO> getCurrentUser(Authentication authentication) {
        logger.info("Request for current user by email: " + authentication.getName());
        return ResponseEntity.ok(userService.findByEmail(authentication.getName()));
    }
}
