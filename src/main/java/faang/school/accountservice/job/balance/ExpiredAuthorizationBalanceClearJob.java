package faang.school.accountservice.job.balance;

import faang.school.accountservice.entity.balance.AuthorizationBalance;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiredAuthorizationBalanceClearJob {
    private final BalanceService balanceService;
    @Scheduled(cron = "${jobs.balance.expired.cron}")
    public void correctDraftPosts() {

//        log.info("Starting spellcheck job for draft posts");

        List<AuthorizationBalance> authBalances = balanceService.getExpiredAuthorizationBalance();

//        authBalances.forEach(authBalance -> {
//            authBalance.setType(AuthorizationBalanceType.EXPIRED);
//            authBalance.setExpiresAt(LocalDateTime.now());
//            Balance balance = balanceService.getBalanceById()
//        });

//        log.info("Finished spellcheck job");
    }
}
