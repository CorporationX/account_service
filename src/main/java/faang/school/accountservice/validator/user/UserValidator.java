package faang.school.accountservice.validator.user;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.exception.user.UserNotFoundException;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserValidator {

    private final UserServiceClient userServiceClient;

    public void validateUserExists(Long userId) {
        if (userServiceClient.getUser(userId) != null) {
            userServiceClient.getUser(userId);
        } else {
            throw new UserNotFoundException("Can't find user with id: " + userId);
        }
    }
}