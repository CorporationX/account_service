package faang.school.accountservice.config.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.exception.ErrorResponse;
import faang.school.accountservice.exception.ForbiddenException;
import feign.FeignException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (isSwaggerUri(path)) {
            chain.doFilter(request, response);
            return;
        }

        String userIdHeader = req.getHeader("x-user-id");
        if (userIdHeader == null) {
            sendErrorResponse(res, req, HttpStatus.FORBIDDEN,
                    "'x-user-id' header is missing", ForbiddenException.class);
            return;
        }

        Long userId;
        UserDto user;

        try {
            userId = Long.parseLong(userIdHeader);
            userContext.setUserId(userId);
            user = userServiceClient.getUser(userId);
        } catch (NumberFormatException e) {
            sendErrorResponse(res, req, HttpStatus.BAD_REQUEST, "Invalid user ID format", IllegalArgumentException.class);
            return;
        } catch (FeignException e) {
            sendErrorFeignException(res, req, e);
            return;
        }

        userContext.setUser(user);

        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }

    private <T extends RuntimeException> void sendErrorResponse(HttpServletResponse res,
                                                                HttpServletRequest req,
                                                                HttpStatus status,
                                                                String message,
                                                                Class<T> errorType) throws IOException {
        res.setStatus(status.value());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                req.getRequestURL().toString(),
                errorType.getSimpleName(),
                message,
                status.value()
        );

        res.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private void sendErrorFeignException(HttpServletResponse res,
                                         HttpServletRequest req,
                                         FeignException feignException) throws IOException {
        HttpStatus status = HttpStatus.resolve(feignException.status());
        res.setStatus(status == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : status.value());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse;
        String responseBody = feignException.contentUTF8();

        try {
            errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
        } catch (Exception ex) {
            errorResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    req.getRequestURL().toString(),
                    feignException.getClass().getSimpleName(),
                    feignException.getMessage(),
                    res.getStatus()
            );
        }

        res.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private boolean isSwaggerUri(String path) {
        return path.contains("swagger") || path.contains("api-docs");
    }
}
