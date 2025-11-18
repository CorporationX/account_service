package faang.school.accountservice.config.context;

import faang.school.accountservice.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private final ThreadLocal<UserDto> userHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        return userIdHolder.get();
    }

    public void setUser(UserDto user) {
        userHolder.set(user);
    }

    public UserDto getUser() {
        return userHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
        userHolder.remove();
    }
}
