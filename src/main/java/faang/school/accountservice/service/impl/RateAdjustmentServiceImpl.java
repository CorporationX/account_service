package faang.school.accountservice.service.impl;

import faang.school.accountservice.service.RateAdjustmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateAdjustmentServiceImpl implements RateAdjustmentService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final RateAdjustmentRulesLoader rulesLoader;
    private final NotificationService notificationService; // Опционально
    @Override
    public void adjustRateForAchievement(long userId, String title) {

    }
}
