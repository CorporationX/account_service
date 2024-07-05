package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.SavingsAccountTariffHistory;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRateHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SavingAccountMapperTest {

    @Mock
    private SavingsAccount savingsAccount;

    @Mock
    private SavingsAccountTariffHistory savingsAccountTariffHistory;

    @Mock
    private Tariff tariff;

    @Mock
    private TariffRateHistory tariffRateHistory;

    @Mock
    private Account account;

    @InjectMocks
    private SavingAccountMapperImpl savingAccountMapper;

    private Long tariffId;
    private BigDecimal tariffRate;

    @BeforeEach
    void setUp(){
        tariffId = 2L;
        tariffRate = BigDecimal.valueOf(0.05);
    }

    @Test
    void shouldMapSavingAccountToDto() {
        Long accountId = 1L;
        BigInteger accountNumber = BigInteger.valueOf(12345);
        BigDecimal balance = BigDecimal.valueOf(1000);
        BigDecimal authorizedBalance = BigDecimal.valueOf(2000);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastInterestCalculatedDate = LocalDateTime.now().minusWeeks(1);
        Balance balanceObj = Balance.builder()
                .currentBalance(balance)
                .authorizedBalance(authorizedBalance)
                .build();

        when(account.getBalance()).thenReturn(balanceObj);
        when(savingsAccount.getAccount()).thenReturn(account);
        when(account.getId()).thenReturn(accountId);
        when(account.getNumber()).thenReturn(accountNumber);
        when(account.getBalance()).thenReturn(balanceObj);

        List<SavingsAccountTariffHistory> tariffHistoryList = new ArrayList<>();
        tariffHistoryList.add(savingsAccountTariffHistory);

        when(savingsAccount.getTariffHistory()).thenReturn(tariffHistoryList);
        when(savingsAccount.getLastInterestCalculatedDate()).thenReturn(lastInterestCalculatedDate);
        when(savingsAccount.getCreatedAt()).thenReturn(createdAt);
        when(savingsAccount.getUpdatedAt()).thenReturn(updatedAt);

        when(savingsAccountTariffHistory.getEndDate()).thenReturn(null);
        when(savingsAccountTariffHistory.getTariff()).thenReturn(tariff);

        when(tariff.getId()).thenReturn(tariffId);
        when(tariff.getRateHistory()).thenReturn(List.of(tariffRateHistory));

        when(tariffRateHistory.getRate()).thenReturn(tariffRate);

        SavingsAccountDto result = savingAccountMapper.toDto(savingsAccount);

        assertEquals(accountId, result.getId());
        assertEquals(accountNumber.longValue(), result.getAccountNumber());
        assertEquals(tariffId, result.getCurrentTariffId());
        assertEquals(tariffRate, result.getCurrentTariffRate());
        assertEquals(lastInterestCalculatedDate, result.getLastInterestCalculatedDate());
        assertEquals(balance, result.getCurrentBalance());
        assertEquals(authorizedBalance, result.getAuthorizedBalance());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());
    }

    @Test
    void shouldGetCurrentTariffHistory() {
        SavingsAccountTariffHistory currentHistory = SavingsAccountTariffHistory.builder().endDate(null).build();
        SavingsAccountTariffHistory endedHistory = SavingsAccountTariffHistory.builder().endDate(LocalDateTime.now()).build();
        List<SavingsAccountTariffHistory> tariffHistoryList = List.of(endedHistory, currentHistory);

        when(savingsAccount.getTariffHistory()).thenReturn(tariffHistoryList);
        SavingsAccountTariffHistory result = savingAccountMapper.getCurrentTariffHistory(savingsAccount);
        assertEquals(currentHistory, result);
    }

    @Test
    void shouldGetCurrentTariffHistoryNoCurrentHistory() {
        SavingsAccountTariffHistory endedHistory1 = SavingsAccountTariffHistory.builder().endDate(LocalDateTime.now()).build();
        SavingsAccountTariffHistory endedHistory2 = SavingsAccountTariffHistory.builder().endDate(LocalDateTime.now()).build();
        List<SavingsAccountTariffHistory> tariffHistoryList = List.of(endedHistory1, endedHistory2);

        when(savingsAccount.getTariffHistory()).thenReturn(tariffHistoryList);
        assertThrows(ResourceNotFoundException.class, () -> savingAccountMapper.getCurrentTariffHistory(savingsAccount));
    }

    @Test
    void shouldGetCurrentRate() {
        TariffRateHistory currentRate = TariffRateHistory.builder().rate(tariffRate).createdAt(LocalDateTime.now()).build();
        TariffRateHistory oldRate = TariffRateHistory.builder().rate(BigDecimal.valueOf(0.04)).createdAt(LocalDateTime.now().minusDays(10)).build();
        List<TariffRateHistory> rateHistoryList = List.of(oldRate, currentRate);

        Tariff tariff = Tariff.builder().rateHistory(rateHistoryList).build();
        BigDecimal result = savingAccountMapper.getCurrentRate(tariff);
        assertEquals(tariffRate, result);
    }

    @Test
    void shouldGetCurrentRateNoRateHistory() {
        Tariff tariff = Tariff.builder().rateHistory(Collections.emptyList()).build();
        assertThrows(ResourceNotFoundException.class, () -> savingAccountMapper.getCurrentRate(tariff));
    }

    @Test
    void shouldGetCurrentTariffInfo() {
        SavingsAccountTariffHistory currentHistory = SavingsAccountTariffHistory.builder().endDate(null).tariff(tariff).build();
        List<SavingsAccountTariffHistory> tariffHistoryList = List.of(currentHistory);

        when(savingsAccount.getTariffHistory()).thenReturn(tariffHistoryList);
        when(tariff.getId()).thenReturn(tariffId);
        Long result = savingAccountMapper.getCurrentTariffInfo(savingsAccount);
        assertEquals(tariffId, result);
    }

    @Test
    void shouldGetCurrentTariffRate() {
        SavingsAccountTariffHistory currentHistory = SavingsAccountTariffHistory.builder().endDate(null).tariff(tariff).build();
        List<SavingsAccountTariffHistory> tariffHistoryList = List.of(currentHistory);
        List<TariffRateHistory> rateHistoryList = List.of(tariffRateHistory);

        when(savingsAccount.getTariffHistory()).thenReturn(tariffHistoryList);
        when(tariff.getRateHistory()).thenReturn(rateHistoryList);
        when(tariffRateHistory.getRate()).thenReturn(tariffRate);
        BigDecimal result = savingAccountMapper.getCurrentTariffRate(savingsAccount);
        assertEquals(tariffRate, result);
    }
}