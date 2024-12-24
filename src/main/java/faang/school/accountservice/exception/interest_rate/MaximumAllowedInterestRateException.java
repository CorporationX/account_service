package faang.school.accountservice.exception.interest_rate;

public class MaximumAllowedInterestRateException extends RuntimeException {
    public MaximumAllowedInterestRateException(String message) {
        super(message);
    }
}