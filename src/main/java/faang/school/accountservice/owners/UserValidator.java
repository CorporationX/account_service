package faang.school.accountservice.owners;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.enums.OwnerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator implements OwnerValidator {

    private final UserServiceClient userServiceClient;

    @Override
    public OwnerType getOwnerType() {
        return OwnerType.USER;
    }

    @Override
    public boolean checkOwnerId(Long id) {
        UserDto user = userServiceClient.getById(id);
        return !user.username().isBlank();
    }
}
