package faang.school.accountservice.exception;

public class BalanceValidationException extends RuntimeException {
  public BalanceValidationException(String message) {
    super(message);
  }
}
