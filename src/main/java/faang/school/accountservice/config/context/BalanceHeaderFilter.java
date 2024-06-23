package faang.school.accountservice.config.context;

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
public class BalanceHeaderFilter implements Filter {
    private final BalanceContext balanceContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String balanceId = req.getHeader("x-balance-id");
        if (balanceId != null) {
            balanceContext.setBalanceId(Long.parseLong(balanceId));
        }
        try {
            chain.doFilter(request, response);
        } finally {
            balanceContext.clear();
        }
    }
}
