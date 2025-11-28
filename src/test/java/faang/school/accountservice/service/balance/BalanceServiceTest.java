package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.ChangedBalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.exception.InvalidBalanceOperationException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validation.BalanceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    private final static long DEFAULT_ID = 1L;
    private final static BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    private final static LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.now();
    private final static String DEFAULT_ACCOUNT_NUMBER = "12345678901234567890";
    private final static BigDecimal ONE_HUNDRED_AMOUNT = BigDecimal.valueOf(100);
    private final static BigDecimal FIFTY_AMOUNT = BigDecimal.valueOf(50);
    private final static BigDecimal NEGATIVE_AMOUNT = BigDecimal.valueOf(-1);

    private final long balanceId = DEFAULT_ID;
    private final long accountId = DEFAULT_ID;
    private final BigDecimal zeroBalance = DEFAULT_AMOUNT;
    private final BigDecimal oneHundredBalance = ONE_HUNDRED_AMOUNT;
    private final BigDecimal fiftyAmount = FIFTY_AMOUNT;
    private final BigDecimal negativeAmount = NEGATIVE_AMOUNT;
    private final LocalDateTime currentDateTime = DEFAULT_DATE_TIME;
    private final String accountNumber = DEFAULT_ACCOUNT_NUMBER;
    private final long userId = DEFAULT_ID;
    private final OwnerType ownerType = OwnerType.USER;
    private final AccountType accountType = AccountType.CURRENCY;
    private final Currency currency = Currency.USD;
    private final AccountStatus accountStatus = AccountStatus.ACTIVE;

    private final CreateBalanceDto balanceDtoForCreate = CreateBalanceDto.builder()
            .accountId(accountId)
            .actualBalance(oneHundredBalance)
            .authorizationBalance(fiftyAmount)
            .currentDateTime(currentDateTime)
            .build();

    private final UpdateBalanceDto updateBalanceDto = UpdateBalanceDto.builder()
            .accountId(accountId)
            .actualBalance(oneHundredBalance)
            .authorizationBalance(zeroBalance)
            .currentDateTime(currentDateTime)
            .build();

    private final ChangedBalanceDto changedBalanceDto = ChangedBalanceDto.builder()
            .amount(fiftyAmount)
            .build();

    private final ChangedBalanceDto changedBalanceDtoNegativeAmount = ChangedBalanceDto.builder()
            .amount(negativeAmount)
            .build();

    private final Account account = Account.builder()
            .id(accountId)
            .accountNumber(accountNumber)
            .ownerId(userId)
            .ownerType(ownerType)
            .accountType(accountType)
            .currency(currency)
            .status(accountStatus)
            .balance(null)
            .build();

    private final Balance balance = Balance.builder()
            .id(balanceId)
            .account(account)
            .actualBalance(oneHundredBalance)
            .authorizationBalance(fiftyAmount)
            .build();

    private final BalanceDto balanceDto = BalanceDto.builder()
            .id(balanceId)
            .account(account)
            .actualBalance(oneHundredBalance)
            .authorizationBalance(fiftyAmount)
            .build();

    private final BalanceDto balanceDtoAfterAuth = BalanceDto.builder()
            .id(balanceId)
            .account(account)
            .actualBalance(fiftyAmount)
            .authorizationBalance(oneHundredBalance)
            .build();

    private final BalanceDto balanceDtoAfterAuthConfirm = BalanceDto.builder()
            .id(balanceId)
            .account(account)
            .actualBalance(oneHundredBalance)
            .authorizationBalance(zeroBalance)
            .build();

    private final BalanceDto balanceDtoAfterAuthRelease = BalanceDto.builder()
            .id(balanceId)
            .account(account)
            .actualBalance(oneHundredBalance)
            .authorizationBalance(zeroBalance)
            .build();

    private BalanceDto resultBalanceDto;

    @InjectMocks
    private BalanceServiceImpl balanceService;
    @Mock
    private BalanceValidator balanceValidator;
    @Mock
    private BalanceRepository balanceRepository;
    @Spy
    private BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);
    @Captor
    private ArgumentCaptor<Balance> balanceCaptor;

    @Test
    public void testSuccessfullyBalanceCreated() {
        when(balanceValidator.validateAccountExisting(accountId)).thenReturn(account);
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        resultBalanceDto = balanceService.create(balanceDtoForCreate);

        assertEquals(balanceDto, resultBalanceDto);

        verify(balanceRepository, times(1)).save(balanceCaptor.capture());
        Balance savedBalance = balanceCaptor.getValue();
        assertEquals(balance.getActualBalance(), savedBalance.getActualBalance());
        assertEquals(balance.getAccount(), savedBalance.getAccount());
        assertEquals(balance.getAuthorizationBalance(), savedBalance.getAuthorizationBalance());
    }

    @Test
    public void testSuccessfullyBalanceUpdate() {
        when(balanceValidator.validateBalanceExisting(balanceId)).thenReturn(balance);
        when(balanceValidator.validateAccountExisting(accountId)).thenReturn(account);
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        resultBalanceDto = balanceService.update(balanceId, updateBalanceDto);

        verify(balanceRepository, times(1)).save(balanceCaptor.capture());
        Balance updatedBalance = balanceCaptor.getValue();
        assertEquals(updatedBalance.getId(), resultBalanceDto.id());
        assertEquals(updatedBalance.getAccount(), resultBalanceDto.account());
        assertEquals(updatedBalance.getActualBalance(), resultBalanceDto.actualBalance());
        assertEquals(updatedBalance.getAuthorizationBalance(), resultBalanceDto.authorizationBalance());
    }

    @Test
    public void testSuccessfullyBalanceGetById() {
        when(balanceValidator.validateBalanceExisting(balanceId)).thenReturn(balance);
        resultBalanceDto = balanceService.getBalanceById(balanceId);
        assertEquals(balanceDto, resultBalanceDto);
    }

    @Test
    public void testSuccessfullyAuthorizationMade() {
        when(balanceValidator.validateBalanceExisting(balanceId)).thenReturn(balance);
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        resultBalanceDto = balanceService.authorize(balanceId, changedBalanceDto);
        assertEquals(balanceDtoAfterAuth, resultBalanceDto);
    }

    @Test
    public void testSuccessfullyAuthorizationConfirmed() {
        when(balanceValidator.validateBalanceExisting(balanceId)).thenReturn(balance);
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        resultBalanceDto = balanceService.confirm(balanceId, changedBalanceDto);
        assertEquals(balanceDtoAfterAuthConfirm, resultBalanceDto);
    }

    @Test
    public void testSuccessfullyAuthorizationReleased() {
        when(balanceValidator.validateBalanceExisting(balanceId)).thenReturn(balance);
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        resultBalanceDto = balanceService.confirm(balanceId, changedBalanceDto);
        assertEquals(balanceDtoAfterAuthRelease, resultBalanceDto);
    }

    @Test
    public void testFailBalanceCreateWhenAccountDoesNotExist() {
        when(balanceValidator.validateAccountExisting(accountId))
                .thenThrow(new AccountNotFoundException("Account not found"));
        assertThrows(AccountNotFoundException.class, () -> balanceService.create(balanceDtoForCreate));
    }

    @Test
    public void testFailBalanceUpdateWhenBalanceDoesNotExist() {
        when(balanceValidator.validateBalanceExisting(balanceId))
                .thenThrow(new BalanceNotFoundException("Balance not found"));
        assertThrows(BalanceNotFoundException.class, () -> balanceService.update(balanceId, updateBalanceDto));
    }

    @Test
    public void testFailBalanceGetWhenBalanceDoesNotExist() {
        when(balanceValidator.validateBalanceExisting(balanceId))
                .thenThrow(new BalanceNotFoundException("Balance not found"));
        assertThrows(BalanceNotFoundException.class, () -> balanceService.getBalanceById(balanceId));
    }

    @Test
    public void testFailAuthMadeWhenAmountIsNegative() {
        doThrow(new InvalidBalanceOperationException("Amount must be positive"))
                .when(balanceValidator)
                .validateAmount(negativeAmount);
        assertThrows(InvalidBalanceOperationException.class,
                () -> balanceService.authorize(balanceId, changedBalanceDtoNegativeAmount));
    }

    @Test
    public void testFailAuthMadeWhenActualBalanceNotEnough() {
        doThrow(new InsufficientBalanceException("Not enough actual balance"))
                .when(balanceValidator)
                .validateEnoughActualBalance(any(), eq(fiftyAmount));
        assertThrows(InsufficientBalanceException.class,
                () -> balanceService.authorize(balanceId, changedBalanceDto));
    }

    @Test
    public void testFailAuthConfirmWhenAuthorizationBalanceNotEnough() {
        doThrow(new InsufficientBalanceException("Not enough authorization balance"))
                .when(balanceValidator)
                .validateEnoughAuthorizationBalance(any(), eq(fiftyAmount));
        assertThrows(InsufficientBalanceException.class,
                () -> balanceService.confirm(balanceId, changedBalanceDto));
    }
}
