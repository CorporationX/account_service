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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
        Pageable pageable = PageRequest.of(0, BATCH_SIZE, Sort.by("id").ascending());

        when(accountRepository.findActiveAccountsWithCashbackTariff(pageable)).thenReturn(accounts);
        when(accountRepository.findActiveAccountsWithCashbackTariff(
                PageRequest.of(1, BATCH_SIZE, Sort.by("id").ascending())))
                .thenReturn(new ArrayList<>());

        cashbackScheduler.calculateCashback();

        verify(accountRepository, times(2)).findActiveAccountsWithCashbackTariff(any());
        verify(cashbackTariffService).calculateCashback(accounts.get(0), startOfLastMonth, endOfLastMonth);
        verify(cashbackTariffService).calculateCashback(accounts.get(1), startOfLastMonth, endOfLastMonth);
    }
}
