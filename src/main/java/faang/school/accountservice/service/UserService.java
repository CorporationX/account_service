package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ExternalServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;

    UserDto getUserById(long id) {
        try {
            return userServiceClient.getUser(id);
        } catch (FeignException.BadRequest e) {
            throw new EntityNotFoundException("Пользователь не найден");
        } catch (FeignException e) {
            throw new ExternalServiceException("User service недоступен");
        }
    }
}
