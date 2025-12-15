package faang.school.accountservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private final ThreadLocal<String> userRoleHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public Long getUserId() {
        return userIdHolder.get();
    }

    public void setUserRole(String role) {
        userRoleHolder.set(role);
    }

    public String getUserRole() {
        return userRoleHolder.get();
    }

    public boolean hasRole(String role) {
        String currentRole = userRoleHolder.get();
        return currentRole != null && currentRole.equals(role);
    }

    public void clear() {
        userIdHolder.remove();
        userRoleHolder.remove();
    }
}
