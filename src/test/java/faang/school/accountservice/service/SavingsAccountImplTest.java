package faang.school.accountservice.service;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.impl.SavingsAccountImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SavingsAccountImplTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NumberGenerator numberGenerator;

    @Spy
    private SavingsAccountMapper savingsAccountMapper;

    @InjectMocks
    private SavingsAccountImpl savingsAccountService;

    private Account account;
    private SavingsAccount savingsAccount;
    private List<BigDecimal> balances;

    @BeforeEach
    public void setUp() {
        Tariff tariff = new Tariff();
        balances = new ArrayList<>();
        balances.add(BigDecimal.valueOf(1));

        tariff.setId(1L);
        tariff.setType(TariffType.BASE);
        tariff.setCurrentRate(BigDecimal.ONE);

        Balance balance = new Balance();
        balance.setId(1L);
        balance.setCurFactBalance(BigDecimal.ONE);

        account = new Account();
        account.setId(1L);
        account.setType(AccountType.SAVINGS_ACCOUNT);
        account.setBalance(balance);
        account.setNumber("12345678910112");

        savingsAccount = SavingsAccount.builder()
                .id(1L)
                .lastDateBeforeInterest(LocalDateTime.now().minusHours(24))
                .tariff(tariff)
                .account(account)
                .build();

        balance.setAccount(account);
        account.setSavingsAccount(savingsAccount);
    }

    @Test
    public void createSavingsAccount() {
        SavingsAccountDto savingsAccountDto = new SavingsAccountDto();

        when(savingsAccountMapper.toEntity(savingsAccountDto)).thenReturn(savingsAccount);
        when(numberGenerator.prepareNumberForAccount()).thenReturn("12345678910112");
        SavingsAccountDto result = savingsAccountService.createSavingsAccount(savingsAccountDto);

        assertEquals(savingsAccountDto, result);
        assertNotNull(savingsAccount.getAccount().getNumber());
        verify(savingsAccountRepository).save(savingsAccount);
    }

    @Test
    public void getSavingsAccountByIdTest() {
        when(savingsAccountRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));

        SavingsAccountDto accountDto = prepareSavingsAccountDto();
        SavingsAccountDto result = savingsAccountService.getSavingsAccountById(1L);

        assertEquals(accountDto, result);
    }

    @Test
    public void getSavingsAccountByUserUdTest() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        SavingsAccountDto accountDto = prepareSavingsAccountDto();
        SavingsAccountDto result = savingsAccountService.getSavingsAccountByUserId(1L);

        assertEquals(accountDto, result);
    }

    private SavingsAccountDto prepareSavingsAccountDto() {
        SavingsAccountDto accountDto = new SavingsAccountDto();
        accountDto.setAccountId(account.getId());
        accountDto.setNumber(null);
        accountDto.setTariffId(1L);
        accountDto.setBettingHistory(null);
        return accountDto;
    }

}
