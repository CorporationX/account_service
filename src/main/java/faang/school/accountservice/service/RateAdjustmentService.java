package faang.school.accountservice.service;

public interface RateAdjustmentService {
    boolean adjustRate(long userId, double rateChange);
}
