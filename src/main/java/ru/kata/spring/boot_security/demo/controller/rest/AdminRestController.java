package ru.kata.spring.boot_security.demo.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.DTO.UserCreateDTO;
import ru.kata.spring.boot_security.demo.DTO.UserReadDTO;
import ru.kata.spring.boot_security.demo.DTO.UserUpdateDTO;
import ru.kata.spring.boot_security.demo.service.DTO.UserDTOService;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final UserDTOService userService;

    public AdminRestController(UserDTOService userService) {
        logger.info("hello from AdminController");
        this.userService = userService;
    }

    // 200 - done - TODO - todo&check
    @GetMapping("/users")
    public ResponseEntity<List<UserReadDTO>> getUsers(Authentication authentication) {
        logger.info("getUsers called in AdminRest Controller");
        logger.info("Request for current user by email: " + authentication.getName());
        return ResponseEntity.ok(userService.findAll());
    }

//    // 200 - done - TODO - todo&check
//    @GetMapping
//    public ResponseEntity<UserReadDTO> showUserUpdatePage(@RequestParam Long id) {
//        logger.info("showUserUpdatePage called in AdminRestController");
//        return ResponseEntity.ok(userService.findById(id));
//    }

    // 200 or 404 - TODO - todo&check
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserReadDTO> getUserById(@PathVariable Long id) {
        logger.info("findUserById called in AdminRestController");
        return ResponseEntity.ok(userService.findById(id));
    }

    // 201 - in proc - TODO - todo&check
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserReadDTO> createUser(@RequestBody UserCreateDTO user) {
        logger.info("addUser called in AdminRestController");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(user));
    }

    // 200 - TODO - todo&check
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserReadDTO> updateUser(@PathVariable Long id,
                                                  @RequestBody UserUpdateDTO user) {
        userService.update(id, user);
        return ResponseEntity.ok(userService.findById(id));
    }

    // 204 - done  - TODO - todo&check
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("deleteUser called in AdminRestController");
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
