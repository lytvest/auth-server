package ru.lytvest.auth.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OAuth2ErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Определяем статус ошибки
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = status != null ? HttpStatus.valueOf(Integer.parseInt(status.toString())) : HttpStatus.INTERNAL_SERVER_ERROR;

        // Получаем исключение (если есть)
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String errorMessage = "Произошла неожиданная ошибка.";
        String errorDetails = "";

        // Пытаемся извлечь OAuth2-ошибку
        if (exception instanceof OAuth2Error oauth2Error) {
            errorMessage = "Ошибка OAuth2: " + oauth2Error.getDescription();
            errorDetails = String.format(
                "Код ошибки: %s\nОписание: %s\nURI: %s",
                oauth2Error.getErrorCode(),
                oauth2Error.getDescription(),
                oauth2Error.getUri() != null ? oauth2Error.getUri() : "не указан"
            );
        } else if (exception instanceof Exception) {
            errorMessage = "Системная ошибка: " + ((Exception) exception).getMessage();
            errorDetails = getStackTraceAsString(exception);
        } else {
            // Нет исключения — смотрим параметры запроса
            errorMessage = "Запрос не может быть обработан.";
            errorDetails = "Проверьте параметры запроса (client_id, redirect_uri, scope и т.д.).";
        }

        // Передаём данные в шаблон
        model.addAttribute("statusCode", httpStatus.value());
        model.addAttribute("statusReason", httpStatus.getReasonPhrase());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorDetails", errorDetails);
        model.addAttribute("requestParams", getRequestParameters(request));

        return "error/oauth2-error"; // путь к Thymeleaf-шаблону
    }

    private String getStackTraceAsString(Object exception) {
        if (exception instanceof Throwable throwable) {
            StringBuilder sb = new StringBuilder();
            sb.append(throwable.toString()).append("\n");
            for (StackTraceElement element : throwable.getStackTrace()) {
                sb.append("  at ").append(element.toString()).append("\n");
            }
            return sb.toString();
        }
        return exception != null ? exception.toString() : "Неизвестная ошибка";
    }

    private Map<String, String[]> getRequestParameters(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return params != null ? params : Collections.emptyMap();
    }
}