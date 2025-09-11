package faang.school.accountservice.exception;

public class SequenceLockException extends AccountNumberGenerationException {
  public SequenceLockException(String message, Throwable cause) {
    super(message, cause);
  }
}