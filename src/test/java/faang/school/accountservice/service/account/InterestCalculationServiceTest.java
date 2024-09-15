package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.TariffAndRateDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.repository.account.SavingsAccountRepository;
import faang.school.accountservice.validator.InterestCalculationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InterestCalculationServiceTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private SavingsAccountService savingsAccountService;

    @Mock
    private InterestCalculationValidator interestCalculationValidator;

    @InjectMocks
    private InterestCalculationService interestCalculationService;

    private UUID uuidOne;
    private UUID uuidTwo;

    private Account accountOne;
    private Account accountTwo;

    private SavingsAccount savingsAccountOne;
    private SavingsAccount savingsAccountTwo;

    private TariffAndRateDto tariffAndRateDtoOne;
    private TariffAndRateDto tariffAndRateDtoTwo;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        uuidOne = UUID.randomUUID();
        uuidTwo = UUID.randomUUID();

        accountOne = new Account();
        accountOne.setId(uuidOne);
        accountOne.setBalance(BigDecimal.valueOf(1000));

        accountTwo = new Account();
        accountTwo.setId(uuidOne);
        accountTwo.setBalance(BigDecimal.valueOf(1000));

        savingsAccountOne = new SavingsAccount();
        savingsAccountOne.setId(uuidOne);
        savingsAccountOne.setLastInterestCalculationDate(LocalDateTime.now().minusDays(30));
        savingsAccountOne.setAccount(accountOne);
        tariffAndRateDtoOne = new TariffAndRateDto();
        tariffAndRateDtoOne.setRate("10");

        savingsAccountTwo = new SavingsAccount();
        savingsAccountTwo.setId(uuidTwo);
        savingsAccountTwo.setLastInterestCalculationDate(LocalDateTime.now().minusDays(30));
        savingsAccountTwo.setAccount(accountTwo);

        tariffAndRateDtoTwo = new TariffAndRateDto();
        tariffAndRateDtoTwo.setRate("20");
    }

    @Test
    void testCalculateInterest() {
        List<SavingsAccount> accounts = Arrays.asList(savingsAccountOne, savingsAccountTwo);

        when(savingsAccountRepository.findAll()).thenReturn(accounts);
        when(interestCalculationValidator.validateLastInterestCalculationDate(any())).thenReturn(true);
        when(savingsAccountService.getSavingAccountById(uuidOne)).thenReturn(tariffAndRateDtoOne);
        when(savingsAccountService.getSavingAccountById(uuidTwo)).thenReturn(tariffAndRateDtoTwo);

        interestCalculationService.calculateInterest();

        verify(savingsAccountRepository, times(1)).findAll();
        verify(savingsAccountRepository, times(2)).save(any(SavingsAccount.class));
        verify(interestCalculationValidator, times(2)).validateLastInterestCalculationDate(any(LocalDateTime.class));

        BigDecimal expectedBalanceAccount1 = BigDecimal.valueOf(1100);
        BigDecimal expectedBalanceAccount2 = BigDecimal.valueOf(1200);

        assertEquals(expectedBalanceAccount1, savingsAccountOne.getAccount().getBalance());
        assertEquals(expectedBalanceAccount2, savingsAccountTwo.getAccount().getBalance());
    }
}
