package ru.kata.spring.boot_security.demo.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

// определяет логику перенаправления пользователя после успешного входа в систему
@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    private final Logger logger = Logger.getLogger(SuccessUserHandler.class.getName());

    private final ObjectMapper objectMapper = new ObjectMapper();

    // вызывается Spring Security после успешной аутентификации пользователя
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException {
        logger.info("onAuthenticationSuccess called in successUserHandler");

        // получаем коллекцию GrantedAuthority объектов (роли/права пользователя) и конвертируем в Set для удобства работы
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // создаём тело ответа (body для response)
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("status", "success");
        responseBody.put("username", authentication.getName());
        responseBody.put("roles", roles);
        // добавление временной метки для упрощения отладки
        responseBody.put("timestamp", Instant.now().toString());

        // настраиваем HTTP-ответ перед отправкой данных клиенту
        // явная установка HTTP-статус-кода, чтобы сделать код предсказуемым
        httpServletResponse.setStatus(HttpStatus.OK.value());

        // устанавливаем MIME-тип содержимого ответа, говорим клиенту, что тело ответа содержит JSON
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // повышаем безопасность
        // даже если токен не возвращается в ответе, метаданные не должны кешироваться
        httpServletResponse.setHeader("Cache-Control", "no-store");

        //
        objectMapper.writeValue(httpServletResponse.getWriter(), responseBody);
    }
}