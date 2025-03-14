package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceReadDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.mapper.BalanceMapperImpl;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceMapperImpl balanceMapper;

    @InjectMocks
    private BalanceService balanceService;

    private BalanceCreateDto createDto;
    private Balance balance;

    @BeforeEach
    public void beforeEach() {
        Account account = Account.builder()
                .id(1L)
                .build();

        balance = Balance.builder()
                .id(1L)
                .actualBalance(BigDecimal.valueOf(100))
                .authorizedBalance(BigDecimal.valueOf(50))
                .account(account)
                .build();
    }


    @Test
    public void testIncreaseBalanceSuccessCase() {
        Mockito.when(balanceRepository.getReferenceById(1L)).thenReturn(balance);

        BigDecimal resultActualBalance = balanceService.increaseBalance(1L, BigDecimal.valueOf(100))
                .getActualBalance();
        BigDecimal expectedActualBalance = BigDecimal.valueOf(200);

        assertEquals(expectedActualBalance, resultActualBalance);
    }

    @Test
    public void testDecreaseBalanceSuccessCase() {
        Mockito.when(balanceRepository.getReferenceById(1L)).thenReturn(balance);

        BigDecimal resultActualBalance = balanceService.decreaseBalance(1L, BigDecimal.valueOf(100))
                .getActualBalance();
        BigDecimal expectedActualBalance = BigDecimal.ZERO;

        assertEquals(expectedActualBalance, resultActualBalance);
    }

    @Test
    public void testDecreaseBalanceInsufficientFunds() {
        Mockito.when(balanceRepository.getReferenceById(1L)).thenReturn(balance);

        assertThrows(BusinessException.class,
                () -> balanceService.decreaseBalance(1L, BigDecimal.valueOf(150)));
    }

    @Test
    public void testReserveBalanceSuccessCase() {
        Mockito.when(balanceRepository.getReferenceById(1L)).thenReturn(balance);

        BalanceReadDto readDto = balanceService.reserveBalance(1L, BigDecimal.valueOf(100));
        BigDecimal resultActualBalance = readDto.getActualBalance();
        BigDecimal expectedActualBalance = BigDecimal.ZERO;

        BigDecimal resultAuthorizedBalance = readDto.getAuthorizedBalance();
        BigDecimal expectedAuthorizedBalance = BigDecimal.valueOf(150);

        assertEquals(expectedActualBalance, resultActualBalance);
        assertEquals(expectedAuthorizedBalance, resultAuthorizedBalance);
    }

    @Test
    public void testReserveBalanceInsufficientFunds() {
        Mockito.when(balanceRepository.getReferenceById(1L)).thenReturn(balance);

        assertThrows(BusinessException.class,
                () -> balanceService.reserveBalance(1L, BigDecimal.valueOf(150)));
    }

    @Test
    public void testReleaseReservedBalanceSuccessCase() {
        Mockito.when(balanceRepository.getReferenceById(1L)).thenReturn(balance);

        BigDecimal resultAuthorizedBalance = balanceService.releaseReservedBalance(1L, BigDecimal.valueOf(50))
                .getAuthorizedBalance();
        BigDecimal expectedAuthorizedBalance = BigDecimal.ZERO;

        assertEquals(expectedAuthorizedBalance, resultAuthorizedBalance);
    }

    @Test
    public void testReleaseReservedBalanceInsufficientFunds() {
        Mockito.when(balanceRepository.getReferenceById(1L)).thenReturn(balance);

        assertThrows(BusinessException.class,
                () -> balanceService.releaseReservedBalance(1L, BigDecimal.valueOf(150)));
    }

    @Test
    public void testCancelBalanceReservation() {
        Mockito.when(balanceRepository.getReferenceById(1L)).thenReturn(balance);

        BalanceReadDto readDto = balanceService.cancelBalanceReservation(1L);
        BigDecimal resultActualBalance = readDto.getActualBalance();
        BigDecimal expectedActualBalance = BigDecimal.valueOf(150);

        BigDecimal resultAuthorizedBalance = readDto.getAuthorizedBalance();
        BigDecimal expectedAuthorizedBalance = BigDecimal.ZERO;

        assertEquals(expectedActualBalance, resultActualBalance);
        assertEquals(expectedAuthorizedBalance, resultAuthorizedBalance);
    }
}
