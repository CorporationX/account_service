package faang.school.accountservice.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(UUID accountId) {
    super("Account " + accountId + " not found");
  }
}
