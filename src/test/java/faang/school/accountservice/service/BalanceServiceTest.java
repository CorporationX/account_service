package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.mapper.BalanceMapperImpl;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @Mock
    private BalanceRepository balanceRepository;
    @Spy
    private BalanceMapperImpl balanceMapper;
    @InjectMocks
    private BalanceService balanceService;

    private Account account;
    private Balance balance;

    @BeforeEach
    public void init() {
        account = Account.builder()
                .id(2)
                .number("1234").build();
        balance = Balance.builder()
                .account(account)
                .id(2)
                .actualBalance(new BigDecimal(300))
                .authorizationBalance(BigDecimal.valueOf(300))
                .build();
    }

    @Test
    public void writeOffClearingBalanceTest() {
        BalanceDto balanceDto = BalanceDto.builder()
                .accountNumber("1234")
                .actualBalance(new BigDecimal(100.0)).build();
        BalanceDto correctBalance = BalanceDto.builder()
                .accountNumber("1234")
                .actualBalance(new BigDecimal(200))
                .authorizationBalance(new BigDecimal(300)).build();

        when(balanceRepository.findByAccountNumber(anyString())).thenReturn(balance);
        Assertions.assertEquals(correctBalance, balanceService.writeOffClearingBalance(balanceDto));
    }

    @Test
    public void writeOffAuthorizationBalanceTest() {
        BalanceDto balanceDto = BalanceDto.builder()
                .accountNumber("1234")
                .authorizationBalance(new BigDecimal(100.0)).build();
        BalanceDto correctBalance = BalanceDto.builder()
                .accountNumber("1234")
                .actualBalance(new BigDecimal(300))
                .authorizationBalance(new BigDecimal(200)).build();

        when(balanceRepository.findByAccountNumber(anyString())).thenReturn(balance);
        Assertions.assertEquals(correctBalance, balanceService.writeOffAuthorizationBalance(balanceDto));
    }

    @Test
    public void writeOffAuthorizationBalanceTestIllegalArgument() {
        Balance balanceLittle = Balance.builder()
                .account(account)
                .id(2)
                .actualBalance(new BigDecimal(100))
                .authorizationBalance(new BigDecimal(90))
                .build();

        BalanceDto balanceDto = BalanceDto.builder()
                .accountNumber("1234")
                .actualBalance(new BigDecimal(100.0))
                .authorizationBalance(new BigDecimal(100)).build();

        when(balanceRepository.findByAccountNumber(anyString())).thenReturn(balanceLittle);
        Assertions.assertThrows(IllegalArgumentException.class, () -> balanceService.writeOffAuthorizationBalance(balanceDto));
    }

    @Test
    public void addingToBalanceTest() {
        BalanceDto balanceDto = BalanceDto.builder()
                .accountNumber("1234")
                .actualBalance(new BigDecimal(100.0)).build();
        BalanceDto correctBalance = BalanceDto.builder()
                .accountNumber("1234")
                .actualBalance(new BigDecimal(400))
                .authorizationBalance(new BigDecimal(400)).build();

        when(balanceRepository.findByAccountNumber(anyString())).thenReturn(balance);
        Assertions.assertEquals(correctBalance, balanceService.addingToBalance(balanceDto));
    }

    @Test
    public void comparisonOfBalancesTest() {
        Balance balanceNotEquals = Balance.builder()
                .actualBalance(new BigDecimal(100))
                .authorizationBalance(new BigDecimal(110)).build();
        when(balanceRepository.findByAccountNumber(anyString())).thenReturn(balance);
        Assertions.assertDoesNotThrow(() -> balanceService.comparisonOfBalances("1234"));
    }
}
