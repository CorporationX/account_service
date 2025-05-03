package faang.school.accountservice.config.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter extends OncePerRequestFilter {

    private final UserContext userContext;

    private static final List<String> SWAGGER_PATHS = List.of(
            "/swagger-ui",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "/webjars/",
            "/favicon.ico",
            "/swagger-resources",
            "/csrf"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return SWAGGER_PATHS.stream()
                .anyMatch(path -> request.getRequestURI().startsWith(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader("x-user-id");
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required header 'x-user-id'. Please include 'x-user-id' header with a valid user ID in your request.");
        }

        try {
            long userIdLong = Long.parseLong(userId.trim());
            userContext.setUserId(userIdLong);
            filterChain.doFilter(request, response);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format. User ID must be a number.");
        } finally {
            userContext.clear();
        }
    }
}
