package faang.school.accountservice.exception.accountnumber;

public class NoAvailableAccountNumberException extends RuntimeException {

  public NoAvailableAccountNumberException(String message) {
      super(message);
    }
}
