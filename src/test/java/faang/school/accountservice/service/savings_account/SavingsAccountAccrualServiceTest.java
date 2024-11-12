package faang.school.accountservice.service.savings_account;

import faang.school.accountservice.entity.savings_account.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static faang.school.accountservice.util.fabrics.SavingsAccountFabric.buildSavingsAccount;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SavingsAccountAccrualServiceTest {
    @Mock
    private SavingsAccountService savingsAccountService;

    @InjectMocks
    private SavingsAccountAccrualService savingsAccountAccrualService;

    @BeforeEach
    void setUpEach() {
        ExecutorService calculateAccrualsThreadPool = Executors.newSingleThreadExecutor();
        ReflectionTestUtils.setField(savingsAccountAccrualService, "calculateAccrualsThreadPool", calculateAccrualsThreadPool);
    }

    @Test
    void testMakeAccruals() {
        SavingsAccount savingsAccount = buildSavingsAccount();

        when(savingsAccountService.getAllActive()).thenReturn(List.of(savingsAccount));

        savingsAccountAccrualService.makeAccruals();
    }
}
