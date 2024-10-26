package faang.school.accountservice.service.impl.balance;

import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.mapper.BalanceMapperImpl;
import faang.school.accountservice.model.dto.balance.BalanceDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {
    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceMapperImpl balanceMapper;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Balance balance1;
    private Balance balance2;

    @BeforeEach
    void setUp() {
        var account1 = Account.builder()
                .id(1L)
                .accountNumber("234312346242")
                .build();

        var account2 = Account.builder()
                .id(2L)
                .accountNumber("153264375432")
                .build();

        balance1 = Balance.builder()
                .id(1L)
                .account(account1)
                .currentAuthorizationBalance(BigDecimal.ZERO)
                .currentActualBalance(BigDecimal.valueOf(100000.00))
                .createdAt(LocalDateTime.parse("2024-06-01T15:00:00.0000"))
                .updatedAt(LocalDateTime.parse("2024-06-02T15:00:00.0000"))
                .version(0L)
                .build();

        balance2 = Balance.builder()
                .id(2L)
                .account(account2)
                .currentAuthorizationBalance(BigDecimal.ZERO)
                .currentActualBalance(BigDecimal.valueOf(50000.00))
                .createdAt(LocalDateTime.parse("2024-07-01T15:00:00.0000"))
                .updatedAt(LocalDateTime.parse("2024-07-02T15:00:00.0000"))
                .version(0L)
                .build();
    }

    @Test
    @DisplayName("Get Balance test - Success")
    void  testGetBalanceSuccess() {
        var expected = BalanceDto.builder()
                .id(1L)
                .accountId(1L)
                .currentAuthorizationBalance(BigDecimal.ZERO)
                .currentActualBalance(BigDecimal.valueOf(100000.00))
                .createdAt(LocalDateTime.parse("2024-06-01T15:00:00.0000"))
                .updatedAt(LocalDateTime.parse("2024-06-02T15:00:00.0000"))
                .build();

        doReturn(Optional.of(balance1)).when(balanceRepository).findById(anyLong());
        BalanceDto result = balanceService.getBalance(1);

        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper).toDto(balance1);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Get Balance doesn't exist")
    void testGetBalanceShouldThrowException() {
        doReturn(Optional.empty()).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                balanceService.getBalance(anyLong()));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper, never()).toDto(any(Balance.class));
    }


    @Test
    @DisplayName("Create Balance - Success")
    void testCreateBalanceSuccess() {
        var expected = BalanceDto.builder()
                .id(1L)
                .accountId(1)
                .currentActualBalance(BigDecimal.valueOf(100000.0))
                .currentAuthorizationBalance(BigDecimal.valueOf(0))
                .createdAt(LocalDateTime.parse("2024-06-01T15:00:00.0000"))
                .updatedAt(LocalDateTime.parse("2024-06-02T15:00:00.0000"))
                .build();

        doReturn(balance1).when(balanceRepository).save(any(Balance.class));
        BalanceDto result = balanceService.createBalance(expected);
        verify(balanceRepository).save(any(Balance.class));
        verify(balanceMapper).toDto(any(Balance.class));
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Increase Balance increases a balance successfully")
    void testIncreaseBalanceSuccess() {
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(anyLong());
        doReturn(balance1).when(balanceRepository).save(any(Balance.class));
        BalanceDto result = balanceService.increaseBalance(1L, BigDecimal.valueOf(100000.0));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper).toDto(any(Balance.class));
        verify(balanceRepository).save(any(Balance.class));
        assertThat(result.currentActualBalance()).isEqualTo(BigDecimal.valueOf(200000.0));
    }

    @Test
    @DisplayName("Increase Balance throws exception")
    public void increaseBalanceThrowsException() {
        doReturn(Optional.empty()).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                balanceService.increaseBalance(1L, BigDecimal.valueOf(100000.0)));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper, never()).toDto(any(Balance.class));
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    @DisplayName("Decrease Balance decreases a balance successfully")
    void decreaseBalanceShouldDecreaseBalanceSuccess() {
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(anyLong());
        doReturn(balance1).when(balanceRepository).save(any(Balance.class));
        BalanceDto result = balanceService.decreaseBalance(1L, BigDecimal.valueOf(30000.0));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper).toDto(any(Balance.class));
        verify(balanceRepository).save(any(Balance.class));
        assertThat(result.currentActualBalance()).isEqualTo(BigDecimal.valueOf(70000.0));
    }

    @Test
    @DisplayName("Decrease Balance throws exception")
    void decreaseBalanceThrowsException() {
        doReturn(Optional.empty()).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                balanceService.decreaseBalance(1L, BigDecimal.valueOf(30000.0)));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper, never()).toDto(any(Balance.class));
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    @DisplayName("Reserve Balance - Success")
    void testReserveBalanceSuccess() {
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(anyLong());
        doReturn(balance1).when(balanceRepository).save(any(Balance.class));
        BalanceDto result = balanceService.reserveBalance(1L, BigDecimal.valueOf(30000.0));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper).toDto(any(Balance.class));
        verify(balanceRepository).save(any(Balance.class));
        assertThat(result.currentActualBalance()).isEqualTo(BigDecimal.valueOf(70000.0));
        assertThat(result.currentAuthorizationBalance()).isEqualTo(BigDecimal.valueOf(30000.0));
    }

    @Test
    @DisplayName("Reserve balance throws entity not found")
    void releaseBalanceThrowsEntityNotFoundException() {
        doReturn(Optional.empty()).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                balanceService.reserveBalance(1L, BigDecimal.valueOf(30000.0)));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper, never()).toDto(any(Balance.class));
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    @DisplayName("Reservation balance throws InsufficientBalanceException")
    void releaseBalanceThrowsInsufficientBalanceException() {
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(InsufficientBalanceException.class).isThrownBy(() ->
                balanceService.reserveBalance(1L, BigDecimal.valueOf(300000.0)))
                .withMessage("Insufficient funds on balance for reservation");
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper, never()).toDto(any(Balance.class));
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    @DisplayName("Release Balance releases balance successfully")
    void releaseBalanceReleasesSuccessfully() {
        balance1.setCurrentAuthorizationBalance(BigDecimal.valueOf(50000.0));
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(anyLong());
        doReturn(balance1).when(balanceRepository).save(any(Balance.class));
        BalanceDto result = balanceService.releaseReservedBalance(1L, BigDecimal.valueOf(50000.0));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper).toDto(any(Balance.class));
        verify(balanceRepository).save(any(Balance.class));
        assertThat(result.currentActualBalance()).isEqualTo(BigDecimal.valueOf(100000.0));
        assertThat(result.currentAuthorizationBalance()).isEqualTo(BigDecimal.valueOf(0.0));

    }

    @Test
    @DisplayName("Release Balance releases throws Entity Not Found Exception")
    void releaseBalanceReleasesThrowsEntityNotFoundException() {
        doReturn(Optional.empty()).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                balanceService.releaseReservedBalance(1L, BigDecimal.valueOf(50000.0)));
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper, never()).toDto(any(Balance.class));
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    @DisplayName("Release Balance releases throws InsufficientBalanceException")
    void releaseBalanceReleasesThrowsInsufficientBalanceException() {
        balance1.setCurrentAuthorizationBalance(BigDecimal.valueOf(50000.0));
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(InsufficientBalanceException.class).isThrownBy(() ->
                balanceService.releaseReservedBalance(1L, BigDecimal.valueOf(70000.0)))
                .withMessage("Cannot release more than the reserved amount");
        verify(balanceRepository).findById(anyLong());
        verify(balanceMapper, never()).toDto(any(Balance.class));
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    @DisplayName("Transfer was successfully")
    void cancelReservationShouldTransferSuccessfully() {
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(1L);
        doReturn(Optional.of(balance2)).when(balanceRepository).findById(2L);
        doReturn(balance1).when(balanceRepository).save(balance1);
        doReturn(balance2).when(balanceRepository).save(balance2);

        balanceService.transferBalance(1, 2, BigDecimal.valueOf(30000.0));
        verify(balanceRepository).findById(1L);
        verify(balanceRepository).findById(2L);
        verify(balanceRepository).save(balance1);
        verify(balanceRepository).save(balance2);
        verify(balanceMapper).toTransferResultDto(balance1, balance2);
        assertThat(balance1.getCurrentActualBalance()).isEqualTo(BigDecimal.valueOf(70000.0));
        assertThat(balance2.getCurrentActualBalance()).isEqualTo(BigDecimal.valueOf(80000.0));
    }

    @Test
    @DisplayName("Transfer throws IllegalArgumentException")
    void transferShouldThrowIllegalArgumentException() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                balanceService.transferBalance(1, 1, BigDecimal.valueOf(30000.0)))
                .withMessage("Cannot transfer to same account");
        verify(balanceRepository, never()).findById(1L);
        verify(balanceRepository, never()).save(balance1);
        verify(balanceRepository, never()).save(balance2);
        verify(balanceMapper, never()).toTransferResultDto(balance1, balance2);
    }

    @Test
    @DisplayName("Transfer throws EntityNotFoundException")
    void transferShouldThrowEntityNotFoundException() {
        doReturn(Optional.empty()).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                balanceService.transferBalance(1, 2, BigDecimal.valueOf(30000.0)));
        verify(balanceRepository).findById(anyLong());
        verify(balanceRepository, never()).save(balance1);
        verify(balanceMapper, never()).toTransferResultDto(balance1, balance2);
    }

    @Test
    @DisplayName("Transfer throws InsufficientBalanceException")
    void transferShouldThrowInsufficientBalanceException() {
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(1L);
        doReturn(Optional.of(balance2)).when(balanceRepository).findById(2L);

        assertThatExceptionOfType(InsufficientBalanceException.class).isThrownBy(() ->
                balanceService.transferBalance(1, 2, BigDecimal.valueOf(130000.0)))
                .withMessage("Insufficient funds on balance for transfer");
        verify(balanceRepository).findById(1L);
        verify(balanceRepository).findById(2L);
        verify(balanceRepository, never()).save(balance1);
        verify(balanceRepository, never()).save(balance2);
        verify(balanceMapper, never()).toTransferResultDto(balance1, balance2);
    }

    @Test
    @DisplayName("Cancel reservation cancels reservation successfully")
    void cancelReservationCancelsSuccessfully() {
        balance1.setCurrentAuthorizationBalance(BigDecimal.valueOf(50000.0));
        doReturn(Optional.of(balance1)).when(balanceRepository).findById(anyLong());
        doReturn(balance1).when(balanceRepository).save(balance1);

        BalanceDto result = balanceService.cancelReservation(1);
        verify(balanceRepository).findById(1L);
        verify(balanceRepository).save(balance1);
        verify(balanceMapper).toDto(balance1);
        assertThat(result.currentAuthorizationBalance()).isEqualTo(BigDecimal.valueOf(0.0));
        assertThat(result.currentActualBalance()).isEqualTo(BigDecimal.valueOf(150000.0));
    }

    @Test
    @DisplayName("Cancel reservation throws EntityNotFoundException")
    void cancelReservationThrowsEntityNotFoundException() {
        doReturn(Optional.empty()).when(balanceRepository).findById(anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                balanceService.cancelReservation(1));
        verify(balanceRepository).findById(1L);
        verify(balanceRepository, never()).save(balance1);
        verify(balanceMapper, never()).toDto(balance1);
    }
}