package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// указываем, что класс конфигурационный, содержит настройки
@Configuration
// указываем, что включаем настройки безопасности для веб-приложения
@EnableWebSecurity
// включаем поддержку аннотаций для проверки прав доступа на уровне методов
@EnableMethodSecurity
public class WebSecurityConfig {
    private final SuccessUserHandler successUserHandler;

    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    // цепочка фильтров защиты
    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                // отключаем csrf - подделку межсайтовых запросов
//                .csrf(csrf -> csrf.disable())
//                // настройки авторизации, остальное с помощью @PreAuthorize
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/login").permitAll()
//                        .requestMatchers("/user", "/user/**").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/", "/login", "/static/**", "/css/**", "/js/**", "/templates/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                // включаем stateless режим, чтобы сервер не хранил сессию
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                // включаем HTTP Basic аутентификацию с настройками по умолчанию
//                .httpBasic(Customizer.withDefaults())
//                .build();
//    }
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successUserHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }

    // для хэширования паролей
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}