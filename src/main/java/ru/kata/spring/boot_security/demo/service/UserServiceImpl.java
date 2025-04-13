package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        logger.info("hello from UserServiceImpl");
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public void save(User user) {
        logger.info("save was called in UserServiceImpl");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> defaultRole = new HashSet<>();
            defaultRole.add(roleService.findRoleByName("ROLE_USER"));
            user.setRoles(defaultRole);
        }
        userRepository.save(user);
        logger.info("user saved in UserServiceImpl");
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        logger.info("findById was called in UserServiceImpl");
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        logger.info("findAll was called in UserServiceImpl");
        return userRepository.findAll();
    }

    // look at...
    public void update(Long id, User user) {
        logger.info("update was called in UserServiceImpl");
        User u = userRepository.findById(id).orElseThrow();
        if (user.getUsername() != null) {
            u.setUsername(user.getUsername());
        }
        if (user.getLastname() != null) {
            u.setLastname(user.getLastname());
        }
        if (user.getAge() != null) {
            u.setAge(user.getAge());
        }
        if (user.getEmail() != null) {
            u.setEmail(user.getEmail());
        }
        if (user.getPassword() != null || !user.getPassword().isEmpty()) {
            u.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            u.setRoles(user.getRoles());
        }
        userRepository.save(u);
        logger.info("user updated in UserServiceImpl");
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
        logger.info("user deleted in UserServiceImpl");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
//        return user;
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities());
    }

    @Override
    public void saveUserWithRoles(User user, List<String> roleNames) {
        Set<Role> userRoles = new HashSet<>();
        for (String roleName : roleNames) {
            Optional<Role> role = Optional.ofNullable(roleService.findRoleByName(roleName));
            if (role.isPresent()) {
                userRoles.add(role.get());
            } else {
                logger.warning("Role not found: " + roleName);
            }
        }
        user.setRoles(userRoles);
        userRepository.save(user);
    }
}
