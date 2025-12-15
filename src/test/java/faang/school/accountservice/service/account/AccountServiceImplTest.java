package faang.school.accountservice.service.account;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.config.properties.AccountProperties;
import faang.school.accountservice.service.role.RoleService;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountBalanceRepository;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Implementation Tests")
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountProperties accountProperties;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @Mock
    private BalanceService balanceService;

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("Should get account by id successfully")
    void get_WhenAccountExists_ShouldReturnDto() {
        // Given
        Long accountId = 1L;
        Account account = createTestAccount(accountId);
        AccountResponseDto expectedDto = createTestDto(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(expectedDto);

        // When
        AccountResponseDto result = accountService.get(accountId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(accountId);
        verify(accountRepository).findById(accountId);
        verify(accountMapper).toDto(account);
    }

    @Test
    @DisplayName("Should throw exception when account not found by id")
    void get_WhenAccountNotFound_ShouldThrowException() {
        // Given
        Long accountId = 999L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.get(accountId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found");

        verify(accountRepository).findById(accountId);
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should get account by number successfully")
    void getByNumber_WhenAccountExists_ShouldReturnDto() {
        // Given
        String accountNumber = "1234567890123456";
        Account account = createTestAccount(1L, accountNumber);
        AccountResponseDto expectedDto = createTestDto(1L, accountNumber);

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(expectedDto);

        // When
        AccountResponseDto result = accountService.getByNumber(accountNumber);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(accountNumber);
        verify(accountRepository).findByNumber(accountNumber);
        verify(accountMapper).toDto(account);
    }

    @Test
    @DisplayName("Should throw exception when account not found by number")
    void getByNumber_WhenAccountNotFound_ShouldThrowException() {
        // Given
        String accountNumber = "9999999999999999";
        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.getByNumber(accountNumber))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("not found");

        verify(accountRepository).findByNumber(accountNumber);
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should get accounts by owner successfully")
    void getByOwner_WhenAccountsExist_ShouldReturnPage() {
        // Given
        Long ownerId = 100L;
        OwnerType ownerType = OwnerType.USER;
        Pageable pageable = PageRequest.of(0, 10);

        Account account1 = createTestAccount(1L);
        Account account2 = createTestAccount(2L);
        List<Account> accounts = List.of(account1, account2);
        Page<Account> accountPage = new PageImpl<>(accounts, pageable, 2);

        AccountResponseDto dto1 = createTestDto(1L);
        AccountResponseDto dto2 = createTestDto(2L);
        Page<AccountResponseDto> expectedPage = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

        when(userContext.getUserId()).thenReturn(100L);
        when(roleService.isAdmin(100L)).thenReturn(true);
        when(accountRepository.findByOwnerIdAndOwnerType(ownerId, ownerType, pageable))
                .thenReturn(accountPage);
        when(accountMapper.toPageDto(accountPage)).thenReturn(expectedPage);

        // When
        Page<AccountResponseDto> result = accountService.getByOwner(ownerId, ownerType, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(accountRepository).findByOwnerIdAndOwnerType(ownerId, ownerType, pageable);
    }

    @Test
    @DisplayName("Should get active accounts by owner successfully")
    void getActiveByOwner_WhenAccountsExist_ShouldReturnPage() {
        // Given
        Long ownerId = 100L;
        OwnerType ownerType = OwnerType.USER;
        Pageable pageable = PageRequest.of(0, 10);

        Account account = createTestAccount(1L, "1234567890123456", AccountStatus.ACTIVE, Currency.RUB);
        List<Account> accounts = List.of(account);
        Page<Account> accountPage = new PageImpl<>(accounts, pageable, 1);

        AccountResponseDto dto = createTestDto(1L);
        Page<AccountResponseDto> expectedPage = new PageImpl<>(List.of(dto), pageable, 1);

        when(accountRepository.findByOwnerIdAndOwnerTypeAndStatus(
                ownerId, ownerType, AccountStatus.ACTIVE, pageable))
                .thenReturn(accountPage);
        when(accountMapper.toPageDto(accountPage)).thenReturn(expectedPage);

        // When
        Page<AccountResponseDto> result = accountService.getActiveByOwner(ownerId, ownerType, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(accountRepository).findByOwnerIdAndOwnerTypeAndStatus(
                ownerId, ownerType, AccountStatus.ACTIVE, pageable);
    }

    @Test
    @DisplayName("Should get active accounts by owner and currency successfully")
    void getActiveByOwnerAndCurrency_WhenAccountsExist_ShouldReturnPage() {
        // Given
        Long ownerId = 100L;
        OwnerType ownerType = OwnerType.USER;
        Currency currency = Currency.USD;
        Pageable pageable = PageRequest.of(0, 10);

        Account account = createTestAccount(1L, "1234567890123456", AccountStatus.ACTIVE, currency);
        List<Account> accounts = List.of(account);
        Page<Account> accountPage = new PageImpl<>(accounts, pageable, 1);

        AccountResponseDto dto = createTestDto(1L);
        Page<AccountResponseDto> expectedPage = new PageImpl<>(List.of(dto), pageable, 1);

        when(accountRepository.findByOwnerIdAndOwnerTypeAndCurrencyAndStatus(
                ownerId, ownerType, currency, AccountStatus.ACTIVE, pageable))
                .thenReturn(accountPage);
        when(accountMapper.toPageDto(accountPage)).thenReturn(expectedPage);

        // When
        Page<AccountResponseDto> result = accountService.getActiveByOwnerAndCurrency(
                ownerId, ownerType, currency, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(accountRepository).findByOwnerIdAndOwnerTypeAndCurrencyAndStatus(
                ownerId, ownerType, currency, AccountStatus.ACTIVE, pageable);
    }

    @Test
    @DisplayName("Should open account with provided number successfully")
    void open_WhenNumberProvided_ShouldCreateAccount() {
        // Given
        String accountNumber = "1234567890123456";
        OpenAccountDto dto = OpenAccountDto.builder()
                .number(accountNumber)
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .type(AccountType.INDIVIDUAL_CHECKING)
                .currency(Currency.RUB)
                .build();

        Account savedAccount = createTestAccount(1L, accountNumber);
        AccountResponseDto expectedDto = createTestDto(1L, accountNumber);

        when(accountProperties.getMaxActiveAccountsPerOwner()).thenReturn(10);
        when(accountRepository.countByOwnerIdAndOwnerTypeAndStatus(
                dto.getOwnerId(), dto.getOwnerType(), AccountStatus.ACTIVE))
                .thenReturn(5L);
        when(accountRepository.existsByNumber(accountNumber)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountBalanceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountMapper.toDto(savedAccount)).thenReturn(expectedDto);

        // When
        AccountResponseDto result = accountService.open(dto);

        // Then
        assertThat(result).isNotNull();
        verify(accountRepository).countByOwnerIdAndOwnerTypeAndStatus(
                dto.getOwnerId(), dto.getOwnerType(), AccountStatus.ACTIVE);
        verify(accountRepository).existsByNumber(accountNumber);
        verify(accountRepository).save(any(Account.class));
        verify(accountNumberGenerator, never()).generate();
    }

    @Test
    @DisplayName("Should open account with generated number when number not provided")
    void open_WhenNumberNotProvided_ShouldGenerateNumber() {
        // Given
        String generatedNumber = "9876543210987654";
        OpenAccountDto dto = OpenAccountDto.builder()
                .number(null)
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .type(AccountType.INDIVIDUAL_CHECKING)
                .currency(Currency.RUB)
                .build();

        Account savedAccount = createTestAccount(1L, generatedNumber);
        AccountResponseDto expectedDto = createTestDto(1L, generatedNumber);

        when(accountProperties.getMaxActiveAccountsPerOwner()).thenReturn(10);
        when(accountRepository.countByOwnerIdAndOwnerTypeAndStatus(
                dto.getOwnerId(), dto.getOwnerType(), AccountStatus.ACTIVE))
                .thenReturn(5L);
        when(accountNumberGenerator.generate()).thenReturn(generatedNumber);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountBalanceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountMapper.toDto(savedAccount)).thenReturn(expectedDto);

        // When
        AccountResponseDto result = accountService.open(dto);

        // Then
        assertThat(result).isNotNull();
        verify(accountNumberGenerator).generate();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when account limit exceeded")
    void open_WhenAccountLimitExceeded_ShouldThrowException() {
        // Given
        OpenAccountDto dto = OpenAccountDto.builder()
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .type(AccountType.INDIVIDUAL_CHECKING)
                .currency(Currency.RUB)
                .build();

        when(accountProperties.getMaxActiveAccountsPerOwner()).thenReturn(10);
        when(accountRepository.countByOwnerIdAndOwnerTypeAndStatus(
                dto.getOwnerId(), dto.getOwnerType(), AccountStatus.ACTIVE))
                .thenReturn(10L);

        // When & Then
        assertThatThrownBy(() -> accountService.open(dto))
                .isInstanceOf(AccountOperationException.class)
                .hasMessageContaining("Maximum number of active accounts");

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when account number already exists")
    void open_WhenNumberExists_ShouldThrowException() {
        // Given
        String accountNumber = "1234567890123456";
        OpenAccountDto dto = OpenAccountDto.builder()
                .number(accountNumber)
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .type(AccountType.INDIVIDUAL_CHECKING)
                .currency(Currency.RUB)
                .build();

        when(accountProperties.getMaxActiveAccountsPerOwner()).thenReturn(10);
        when(accountRepository.countByOwnerIdAndOwnerTypeAndStatus(
                dto.getOwnerId(), dto.getOwnerType(), AccountStatus.ACTIVE))
                .thenReturn(5L);
        when(accountRepository.existsByNumber(accountNumber)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> accountService.open(dto))
                .isInstanceOf(AccountOperationException.class)
                .hasMessageContaining("already exists");

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should block account successfully")
    void block_WhenAccountIsActive_ShouldBlockAccount() {
        // Given
        Long accountId = 1L;
        Account account = createTestAccount(accountId, "1234567890123456", AccountStatus.ACTIVE, Currency.RUB);
        AccountResponseDto expectedDto = createTestDto(accountId, "1234567890123456", AccountStatus.FROZEN);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(expectedDto);

        // When
        AccountResponseDto result = accountService.block(accountId);

        // Then
        assertThat(result).isNotNull();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);
        verify(accountRepository).findById(accountId);
        verify(accountMapper).toDto(account);
    }

    @Test
    @DisplayName("Should throw exception when account not found for blocking")
    void block_WhenAccountNotFound_ShouldThrowException() {
        // Given
        Long accountId = 999L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.block(accountId))
                .isInstanceOf(AccountNotFoundException.class);

        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("Should throw exception when trying to block closed account")
    void block_WhenAccountIsClosed_ShouldThrowException() {
        // Given
        Long accountId = 1L;
        Account account = createTestAccount(accountId, "1234567890123456", AccountStatus.CLOSED, Currency.RUB);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When & Then
        assertThatThrownBy(() -> accountService.block(accountId))
                .isInstanceOf(AccountOperationException.class)
                .hasMessageContaining("Cannot block closed account");
    }

    @Test
    @DisplayName("Should unblock account successfully")
    void unblock_WhenAccountIsFrozen_ShouldUnblockAccount() {
        // Given
        Long accountId = 1L;
        Account account = createTestAccount(accountId, "1234567890123456", AccountStatus.FROZEN, Currency.RUB);
        AccountResponseDto expectedDto = createTestDto(accountId, "1234567890123456", AccountStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(expectedDto);

        // When
        AccountResponseDto result = accountService.unblock(accountId);

        // Then
        assertThat(result).isNotNull();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        verify(accountRepository).findById(accountId);
        verify(accountMapper).toDto(account);
    }

    @Test
    @DisplayName("Should throw exception when account not found for unblocking")
    void unblock_WhenAccountNotFound_ShouldThrowException() {
        // Given
        Long accountId = 999L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.unblock(accountId))
                .isInstanceOf(AccountNotFoundException.class);

        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("Should throw exception when trying to unblock closed account")
    void unblock_WhenAccountIsClosed_ShouldThrowException() {
        // Given
        Long accountId = 1L;
        Account account = createTestAccount(accountId, "1234567890123456", AccountStatus.CLOSED, Currency.RUB);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When & Then
        assertThatThrownBy(() -> accountService.unblock(accountId))
                .isInstanceOf(AccountOperationException.class)
                .hasMessageContaining("Cannot unblock closed account");
    }

    @Test
    @DisplayName("Should close account with zero balance successfully")
    void close_WhenBalanceIsZero_ShouldCloseAccount() {
        // Given
        Long accountId = 1L;
        Account account = createTestAccount(accountId, "1234567890123456", AccountStatus.ACTIVE, Currency.RUB);
        AccountResponseDto expectedDto = createTestDto(accountId, "1234567890123456", AccountStatus.CLOSED);

        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));
        when(balanceService.getBalanceWithLock(accountId)).thenReturn(BigDecimal.ZERO);
        when(accountMapper.toDto(account)).thenReturn(expectedDto);

        // When
        AccountResponseDto result = accountService.close(accountId);

        // Then
        assertThat(result).isNotNull();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getClosedAt()).isNotNull();
        verify(accountRepository).findByIdWithLock(accountId);
        verify(balanceService).getBalanceWithLock(accountId);
        verify(accountMapper).toDto(account);
    }

    @Test
    @DisplayName("Should throw exception when account not found for closing")
    void close_WhenAccountNotFound_ShouldThrowException() {
        // Given
        Long accountId = 999L;
        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.close(accountId))
                .isInstanceOf(AccountNotFoundException.class);

        verify(accountRepository).findByIdWithLock(accountId);
        verify(balanceService, never()).getBalanceWithLock(any());
    }

    @Test
    @DisplayName("Should throw exception when trying to close account with non-zero balance")
    void close_WhenBalanceIsNotZero_ShouldThrowException() {
        // Given
        Long accountId = 1L;
        BigDecimal nonZeroBalance = new BigDecimal("100.50");
        Account account = createTestAccount(accountId, "1234567890123456", AccountStatus.ACTIVE, Currency.RUB);

        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));
        when(balanceService.getBalanceWithLock(accountId)).thenReturn(nonZeroBalance);

        // When & Then
        assertThatThrownBy(() -> accountService.close(accountId))
                .isInstanceOf(AccountOperationException.class)
                .hasMessage("Cannot close account with non-zero balance");

        verify(accountRepository).findByIdWithLock(accountId);
        verify(balanceService).getBalanceWithLock(accountId);
        assertThat(account.getStatus()).isNotEqualTo(AccountStatus.CLOSED);
    }

    @Test
    @DisplayName("Should throw exception when trying to close already closed account")
    void close_WhenAccountIsClosed_ShouldThrowException() {
        // Given
        Long accountId = 1L;
        Account account = Account.builder()
                .id(accountId)
                .number("1234567890123456")
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .type(AccountType.INDIVIDUAL_CHECKING)
                .currency(Currency.RUB)
                .status(AccountStatus.CLOSED)
                .version(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .closedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));
        when(balanceService.getBalanceWithLock(accountId)).thenReturn(BigDecimal.ZERO);

        // When & Then
        assertThatThrownBy(() -> accountService.close(accountId))
                .isInstanceOf(AccountOperationException.class)
                .hasMessage("Account is already closed");
    }

    // Helper methods

    private Account createTestAccount(Long id) {
        return createTestAccount(id, "1234567890123456", AccountStatus.ACTIVE, Currency.RUB);
    }

    private Account createTestAccount(Long id, String number) {
        return createTestAccount(id, number, AccountStatus.ACTIVE, Currency.RUB);
    }

    private Account createTestAccount(Long id, String number, AccountStatus status, Currency currency) {
        return Account.builder()
                .id(id)
                .number(number)
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .type(AccountType.INDIVIDUAL_CHECKING)
                .currency(currency)
                .status(status)
                .version(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private AccountResponseDto createTestDto(Long id) {
        return createTestDto(id, "1234567890123456", AccountStatus.ACTIVE);
    }

    private AccountResponseDto createTestDto(Long id, String number) {
        return createTestDto(id, number, AccountStatus.ACTIVE);
    }

    private AccountResponseDto createTestDto(Long id, String number, AccountStatus status) {
        return AccountResponseDto.builder()
                .id(id)
                .number(number)
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .type(AccountType.INDIVIDUAL_CHECKING)
                .currency(Currency.RUB)
                .status(status)
                .version(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

