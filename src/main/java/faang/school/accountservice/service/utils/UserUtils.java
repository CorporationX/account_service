package faang.school.accountservice.service.utils;

import faang.school.accountservice.client.UserServiceClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUtils {

  private final UserServiceClient userServiceClient;

  public void validateUser(Long userId) {
    if (userServiceClient.getUser(userId) == null) {
      throw new EntityNotFoundException(String.format("User with ID %d not found", userId));
    }
    log.info("user with id = {} validated", userId);
  }
}
