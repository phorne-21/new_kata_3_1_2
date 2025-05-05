package ru.kata.spring.boot_security.demo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.kata.spring.boot_security.demo.DTO.UserReadDTO;
import ru.kata.spring.boot_security.demo.controller.rest.UserRestController;
import ru.kata.spring.boot_security.demo.service.DTO.UserDTOService;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRestController.class)
public class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDTOService userDTOService;

    private final String TEST_EMAIL = "user@example.com";

    // Успешный запрос данных пользователя
    @Test
    @WithMockUser(username = TEST_EMAIL) // Мокируем аутентифицированного пользователя
    void getCurrentUser_WhenUserExists_ReturnsUser() throws Exception {
        // Given
        UserReadDTO mockUser = new UserReadDTO(
                1L, "John", "Doe", 30, TEST_EMAIL, List.of("ROLE_USER")
        );
        Mockito.when(userDTOService.findByEmail(TEST_EMAIL)).thenReturn(mockUser);

        // When/Then
        mockMvc.perform(get("/user")
                        .with(csrf()) // CSRF-токен для безопасности
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    // Пользователь не найден
    @Test
    @WithMockUser(username = TEST_EMAIL)
    void getCurrentUser_WhenUserNotFound_Returns404() throws Exception {
        // Given
        Mockito.when(userDTOService.findByEmail(TEST_EMAIL))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // When/Then
        mockMvc.perform(get("/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Неавторизованный доступ
    @Test
    void getCurrentUser_WhenUnauthorized_Returns401() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }
}
