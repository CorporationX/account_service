package faang.school.accountservice.entity.savings_account;

import faang.school.accountservice.repository.savings_account.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncSavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;

//    @Scheduled
}
