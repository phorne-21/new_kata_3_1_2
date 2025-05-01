package ru.kata.spring.boot_security.demo.service.DTO;


import ru.kata.spring.boot_security.demo.DTO.UserCreateDTO;
import ru.kata.spring.boot_security.demo.DTO.UserReadDTO;
import ru.kata.spring.boot_security.demo.DTO.UserUpdateDTO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDTOService {
    List<UserReadDTO> findAll();
    UserReadDTO findById(Long id);
    UserReadDTO create(UserCreateDTO userDTO);
    UserReadDTO update(Long id, UserUpdateDTO userDTO);
    void delete(Long id);
    UserReadDTO findByEmail(String email);
    List<Role> getAllRoles();
}
