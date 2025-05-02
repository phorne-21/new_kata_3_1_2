package ru.kata.spring.boot_security.demo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserCreateDTO {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private Integer age;

    @Email
    @NotBlank
    private String email;

    private String password;

    private List<String> roles;

    public UserCreateDTO(String firstName, String lastName, Integer age, String email, String password, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.password = password;
        this.roles = roles.stream().map(Role::getName).collect(Collectors.toList());
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
