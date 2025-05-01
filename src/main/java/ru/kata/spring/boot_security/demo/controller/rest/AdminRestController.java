package ru.kata.spring.boot_security.demo.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.DTO.UserReadDTO;
import ru.kata.spring.boot_security.demo.DTO.UserUpdateDTO;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.DTO.UserDTOService;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/admin")
public class AdminRestController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final UserDTOService userService;

    public AdminRestController(UserDTOService userService) {
        logger.info("hello from AdminController");
        this.userService = userService;
    }

    // 200 - done - TODO - todo&check
    @GetMapping
    public ResponseEntity<List<UserReadDTO>> getUsers(Authentication authentication) {
        logger.info("getUsers called in AdminRest Controller");
        logger.info("Request for current user by email: " + authentication.getName());
        return ResponseEntity.ok(userService.findAll());
    }

    // 201 - in proc - TODO - todo&check
    @PostMapping
    public ResponseEntity<List<UserReadDTO>> addUser(@RequestBody User user) {
        logger.info("addUser called in AdminRestController");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(user));
    }

    // 200 - done - TODO - todo&check
    @GetMapping
    public ResponseEntity<UserReadDTO> showUserUpdatePage(@RequestBody Long id) {
        logger.info("showUserUpdatePage called in AdminRestController");
        return ResponseEntity.ok(userService.findById(id));
    }

    // 200 - TODO - todo&check
    @PutMapping
    public ResponseEntity<UserReadDTO> updateUser(@RequestBody Long id,
                                                  @RequestBody String firstName,
                                                  @RequestBody String lastname,
                                                  @RequestBody String email,
                                                  @RequestBody int age,
                                                  @RequestBody(required = false) String password,
                                                  @RequestBody(required = false) List<String> roles) {
        userService.update(id, new UserUpdateDTO());
        return ResponseEntity.ok(userService.findById(id));
    }

    // 204 - done  - TODO - todo&check
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestBody Long id) {
        logger.info("deleteUser called in AdminRestController");
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 200 or 404 - TODO - todo&check
    @GetMapping("/user/{id}")
    public ResponseEntity<UserReadDTO> findUserById(@PathVariable Long id) {
        logger.info("findUserById called in AdminRestController");
        return ResponseEntity.ok(userService.findById(id));
    }
}
