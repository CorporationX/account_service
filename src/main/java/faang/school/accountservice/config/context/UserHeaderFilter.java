package faang.school.accountservice.config.context;

import faang.school.accountservice.config.openapi.OpenApiConfig;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;
    private final OpenApiConfig openApiConfig;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;

        String requestURI = req.getRequestURI();
        if (openApiConfig.getUris().stream().noneMatch(requestURI::contains)) {
            String userId = req.getHeader("x-user-id");
            if (userId != null) {
                userContext.setUserId(Long.parseLong(userId));
            } else {
                throw new IllegalArgumentException("Missing required header 'x-user-id'." +
                        " Please include 'x-user-id' header with a valid user ID in your request.");
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}
