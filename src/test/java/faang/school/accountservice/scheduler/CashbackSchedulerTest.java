package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.cashback.CashbackTariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CashbackSchedulerTest {
    private final static int BATCH_SIZE = 2;
    private final static int THREAD_POOL = 2;

    @Mock
    private CashbackTariffService cashbackTariffService;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private CashbackScheduler cashbackScheduler;

    private List<Account> accounts = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        Field batchSizeField = CashbackScheduler.class.getDeclaredField("batchSize");
        Field threadPool = CashbackScheduler.class.getDeclaredField("threadPool");
        batchSizeField.setAccessible(true);
        threadPool.setAccessible(true);
        batchSizeField.set(cashbackScheduler, BATCH_SIZE);
        threadPool.set(cashbackScheduler, THREAD_POOL);

        accounts.add(Account.builder()
                .id(UUID.randomUUID())
                .build());

        accounts.add(Account.builder()
                .id(UUID.randomUUID())
                .build());
    }

    @Test
    void testCalculateCashback() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDateTime startOfLastMonth = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonth.atEndOfMonth().atTime(23, 59, 59);

        List<UUID> accountIds = accounts.stream().map((Account::getId)).toList();

        when(accountRepository.findActiveAccountsWithCashbackTariffIds()).thenReturn(accountIds);

        cashbackScheduler.calculateCashback();

        verify(cashbackTariffService).calculateCashback(accounts.get(0).getId(), startOfLastMonth, endOfLastMonth);
        verify(cashbackTariffService).calculateCashback(accounts.get(1).getId(), startOfLastMonth, endOfLastMonth);
    }
}
