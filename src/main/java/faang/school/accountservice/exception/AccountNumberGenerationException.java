package faang.school.accountservice.exception;

/**
 * Исключение для случаев, когда не удается получить уникальный номер счета
 */
public class AccountNumberGenerationException extends RuntimeException {

  public AccountNumberGenerationException(String message) {
    super(message);
  }

  public AccountNumberGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}
