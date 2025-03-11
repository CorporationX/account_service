package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserServiceClient userServiceClient;

    public void validateUser(Long id) {
        if (userServiceClient.getUser(id) == null) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
    }
}
