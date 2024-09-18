package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.BalanceValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);

    @Mock
    private BalanceValidator balanceValidator;

    @InjectMocks
    private BalanceService balanceService;

    private Balance balance;
    private Account account;
    private BalanceDto balanceDto;
    private AccountDto accountDto;

    @BeforeEach
    public void init() {
        account = Account.builder()
                .id(1L)
                .number(BigInteger.TEN)
                .ownerType(OwnerType.PROJECT)
                .ownerProjectId(1L)
                .currency(Currency.USD)
                .accountType(AccountType.INDIVIDUAL)
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.OPEN)
                .version(1L)
                .build();

        UUID balanceId = UUID.randomUUID();

        balance = Balance
                .builder()
                .id(balanceId)
                .authorizedBalance(BigDecimal.valueOf(100))
                .actualBalance(BigDecimal.valueOf(1000))
                .currency(Currency.EUR)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(0L)
                .build();

        balanceDto = BalanceDto
                .builder()
                .id(balanceId)
                .authorizedBalance(BigDecimal.valueOf(100))
                .actualBalance(BigDecimal.valueOf(1000))
                .currency(Currency.EUR)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .accountId(account.getId())
                .build();

        accountDto = AccountDto.builder()
                .number(BigInteger.TEN)
                .ownerType(OwnerType.PROJECT)
                .ownerProjectId(1L)
                .currency(Currency.USD)
                .accountType(AccountType.INDIVIDUAL)
                .accountStatus(AccountStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("Unsuccessfully creation. Cause: balance for account already exists")
    public void testCreateDataValidationException() {
        //when(balanceRepository.getByAccountId(anyLong())).thenReturn(Optional.of(balance));
        //doThrow(DataValidationException.class).when(balanceRepository).getByAccountId(balanceDto.getAccountId());

        doThrow(DataValidationException.class).when(balanceValidator).checkBalanceToCreate(balanceDto);

        assertThrows(DataValidationException.class, () -> balanceService.create(balanceDto));
        verify(balanceRepository, times(0)).create(any(BigDecimal.class), any(BigDecimal.class), anyString(), anyLong());
    }

    @Test
    @DisplayName("Successfully creation")
    public void testCreateSuccess() {
        doNothing().when(balanceValidator).checkBalanceToCreate(balanceDto);
        when(balanceRepository.create(any(BigDecimal.class), any(BigDecimal.class), anyString(), anyLong())).thenReturn(balance);

        BalanceDto actual = balanceService.create(balanceDto);

        Assertions.assertNotNull(actual);
    }

    @Test
    @DisplayName("Unsuccessfully getting dto by id. Cause: balance doesn't exist")
    void testGetByIdResourceNotFoundException() {
        when(balanceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> balanceService.getById(balance.getId()));
        verify(balanceRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Successfully getting dto by id.")
    void testGetByIdSuccess() {
        when(balanceRepository.findById(any(UUID.class))).thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        BalanceDto result = balanceService.getById(balance.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(balanceDto, result);
        verify(balanceRepository, times(1)).findById(balance.getId());
    }

    @Test
    @DisplayName("Unsuccessfully getting entity by id. Cause: balance doesn't exist")
    void testGetEntityByIdResourceNotFoundException() {
        when(balanceRepository.findById(balance.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> balanceService.getEntityById(balance.getId()));
        verify(balanceRepository, times(1)).findById(balance.getId());
    }

    @Test
    @DisplayName("Successfully getting entity by id.")
    void testGetEntityByIdSuccess() {
        when(balanceRepository.findById(any(UUID.class))).thenReturn(Optional.of(balance));

        Balance result = balanceService.getEntityById(balance.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(balance, result);
        verify(balanceRepository, times(1)).findById(balance.getId());
    }

    @Test
    @DisplayName("Successfully updating")
    void testUpdateSuccess() {
        when(balanceRepository.findById(balance.getId())).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        BalanceDto result = balanceService.update(balanceDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(balanceDto, result);
        verify(balanceRepository, times(1)).save(balance);
    }
}
