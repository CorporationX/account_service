package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountPreviewDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.OwnerRequest;
import faang.school.accountservice.event.AccountCreatedEvent;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.cache.AccountCacheableFetcher;
import faang.school.accountservice.service.utils.AccountServiceValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountCacheableFetcher accountFetcher;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AccountServiceValidation accountServiceValidation;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(accountService, "blockSize", 2);
    }

    @Test
    void openAccount_success() {
        // given
        CreateAccountDto createDto = new CreateAccountDto(OwnerType.USER, 10L, AccountType.BUSINESS, "USD");

        Account unsaved = new Account();
        unsaved.setOwnerType(OwnerType.USER);
        unsaved.setOwnerId(10L);
        unsaved.setAccountType(AccountType.BUSINESS);

        Account saved = new Account();
        saved.setId(1L);
        saved.setOwnerType(OwnerType.USER);
        saved.setOwnerId(10L);
        saved.setAccountType(AccountType.BUSINESS);
        saved.setStatus(AccountStatus.ACTIVE);

        AccountDto expectedDto = new AccountDto();
        expectedDto.setId(1L);
        expectedDto.setOwnerId(10L);
        expectedDto.setOwnerType(OwnerType.USER);
        expectedDto.setStatus(AccountStatus.ACTIVE);
        expectedDto.setAccountNumber("ACC-1"); // Example account number
        expectedDto.setBalance(new AccountBalanceDto());
        expectedDto.setUpdatedAt(LocalDateTime.now());

        when(accountMapper.toEntity(createDto)).thenReturn(unsaved);
        when(accountRepository.save(unsaved)).thenReturn(saved);
        when(accountMapper.toDto(saved)).thenReturn(expectedDto);

        // when
        AccountDto result = accountService.openAccount(createDto);

        // then
        assertSame(expectedDto, result);
        verify(accountRepository).save(unsaved);
        verify(eventPublisher).publishEvent(any(AccountCreatedEvent.class));
    }

    @Test
    void freezeAccount_success() {
        // given
        Account account = new Account();
        account.setId(1L);
        account.setOwnerId(10L);
        account.setOwnerType(OwnerType.USER);
        account.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doNothing().when(accountServiceValidation).validateAccount(1L);
        doNothing().when(accountServiceValidation).hasPermission(10L);


        // when
        accountService.freezeAccount(1L);

        // then
        assertEquals(AccountStatus.FROZEN, account.getStatus());
        verify(accountServiceValidation).validateAccount(1L);
        verify(accountServiceValidation).hasPermission(10L);
        verify(eventPublisher).publishEvent(any(AccountCreatedEvent.class));
    }

    @Test
    void freezeAccount_wrongStatus_throwsValidationException() {
        // given
        Account account = new Account();
        account.setId(1L);
        account.setOwnerId(10L);
        account.setOwnerType(OwnerType.USER);
        account.setStatus(AccountStatus.FROZEN);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doNothing().when(accountServiceValidation).validateAccount(1L);
        doNothing().when(accountServiceValidation).hasPermission(10L);

        // then
        assertThrows(DataValidationException.class, () -> accountService.freezeAccount(1L));
        verify(eventPublisher, never()).publishEvent(any(AccountCreatedEvent.class));
    }

    @Test
    void unfreezeAccount_success() {
        // given
        Account account = new Account();
        account.setId(2L);
        account.setOwnerId(11L);
        account.setOwnerType(OwnerType.USER);
        account.setStatus(AccountStatus.FROZEN);

        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));
        doNothing().when(accountServiceValidation).validateAccount(2L);
        doNothing().when(accountServiceValidation).hasPermission(11L);

        // when
        accountService.unfreezeAccount(2L);

        // then
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        verify(accountServiceValidation).validateAccount(2L);
        verify(accountServiceValidation).hasPermission(11L);
        verify(eventPublisher).publishEvent(any(AccountCreatedEvent.class));
    }

    @Test
    void unfreezeAccount_wrongStatus_throwsValidationException() {
        // given
        Account account = new Account();
        account.setId(2L);
        account.setOwnerId(11L);
        account.setOwnerType(OwnerType.USER);
        account.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));
        doNothing().when(accountServiceValidation).validateAccount(2L);
        doNothing().when(accountServiceValidation).hasPermission(11L);


        // then
        assertThrows(DataValidationException.class, () -> accountService.unfreezeAccount(2L));
        verify(eventPublisher, never()).publishEvent(any(AccountCreatedEvent.class));
    }

    @Test
    void closeAccount_success() {
        // given
        Account account = new Account();
        account.setId(3L);
        account.setOwnerId(12L);
        account.setOwnerType(OwnerType.USER);
        account.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(3L)).thenReturn(Optional.of(account));
        doNothing().when(accountServiceValidation).validateAccount(3L);
        doNothing().when(accountServiceValidation).validateCloseAccount(3L, account);

        // when
        accountService.closeAccount(3L);

        // then
        assertEquals(AccountStatus.CLOSED, account.getStatus());
        verify(accountServiceValidation).validateAccount(3L);
        verify(accountServiceValidation).validateCloseAccount(3L, account);
        verify(eventPublisher).publishEvent(any(AccountCreatedEvent.class));
    }

    @Test
    void closeAccount_accountNotFound_throwsAccountNotFoundException() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.closeAccount(99L));
        verify(accountServiceValidation).validateAccount(99L);
        verify(accountServiceValidation, never()).validateCloseAccount(anyLong(), any(Account.class));
        verify(eventPublisher, never()).publishEvent(any(AccountCreatedEvent.class));
    }

    @Test
    void getAccountById_success() {
        // given
        Account account = new Account();
        account.setId(4L);

        AccountDto dto = new AccountDto();

        when(accountRepository.findById(4L)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(dto);

        // when
        AccountDto result = accountService.getAccountById(4L);

        // then
        assertSame(dto, result);
    }

    @Test
    void getAccountById_accountNotFound_throwsAccountNotFoundException() {
        // given
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(99L));
    }

    @Test
    void findAccountsByOwner_returnsPageSlice() {
        OwnerRequest owner = new OwnerRequest(15L, OwnerType.USER);
        Pageable pageable = PageRequest.of(1, 2, Sort.by("id"));

        AccountPreviewDto preview1 = new AccountPreviewDto();
        preview1.setId(100L);
        preview1.setOwnerType(OwnerType.USER);
        preview1.setOwnerId(owner.getId());
        preview1.setAccountType(AccountType.BUSINESS);
        preview1.setStatus(AccountStatus.ACTIVE);

        AccountPreviewDto preview2 = new AccountPreviewDto();
        preview2.setId(101L);
        preview2.setOwnerType(OwnerType.USER);
        preview2.setOwnerId(owner.getId());
        preview2.setAccountType(AccountType.BUSINESS);
        preview2.setStatus(AccountStatus.FROZEN);

        AccountPreviewDto preview3 = new AccountPreviewDto();
        preview3.setId(102L);
        preview3.setOwnerType(OwnerType.USER);
        preview3.setOwnerId(owner.getId());
        preview3.setAccountType(AccountType.BUSINESS);
        preview3.setStatus(AccountStatus.ACTIVE);

        when(accountFetcher.fetchAccountBlock(owner, 1, pageable.getSort()))
                .thenReturn(Arrays.asList(preview1, preview2));
        when(accountFetcher.fetchAccountBlock(owner, 2, pageable.getSort()))
                .thenReturn(Collections.singletonList(preview3));

        List<AccountPreviewDto> result = accountService.findAccountsByOwner(owner, pageable);

        assertEquals(2, result.size());
        assertSame(preview1, result.get(0));
        assertSame(preview2, result.get(1));
        verify(accountFetcher).fetchAccountBlock(owner, 1, pageable.getSort());
        verify(accountFetcher).fetchAccountBlock(owner, 2, pageable.getSort());
    }

    @Test
    void findAccountsByOwner_multipleBlocks_partialResult() {
        OwnerRequest owner = new OwnerRequest(1L, OwnerType.USER);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));

        AccountPreviewDto acc1 = new AccountPreviewDto(); acc1.setId(1L);
        AccountPreviewDto acc2 = new AccountPreviewDto(); acc2.setId(2L);
        AccountPreviewDto acc3 = new AccountPreviewDto(); acc3.setId(3L);
        AccountPreviewDto acc4 = new AccountPreviewDto(); acc4.setId(4L);

        when(accountFetcher.fetchAccountBlock(owner, 0, pageable.getSort()))
                .thenReturn(Arrays.asList(acc1, acc2));
        when(accountFetcher.fetchAccountBlock(owner, 1, pageable.getSort()))
                .thenReturn(Arrays.asList(acc3, acc4));

        List<AccountPreviewDto> result = accountService.findAccountsByOwner(owner, pageable);

        assertEquals(3, result.size());
        assertEquals(acc1, result.get(0));
        assertEquals(acc2, result.get(1));
        assertEquals(acc3, result.get(2));

        verify(accountFetcher).fetchAccountBlock(owner, 0, pageable.getSort());
        verify(accountFetcher).fetchAccountBlock(owner, 1, pageable.getSort());
    }

    @Test
    void findAccountsByOwner_emptyBlocks() {
        OwnerRequest owner = new OwnerRequest(1L, OwnerType.USER);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));

        when(accountFetcher.fetchAccountBlock(owner, 0, pageable.getSort()))
                .thenReturn(Collections.emptyList());
        when(accountFetcher.fetchAccountBlock(owner, 1, pageable.getSort()))
                .thenReturn(Collections.emptyList());

        List<AccountPreviewDto> result = accountService.findAccountsByOwner(owner, pageable);

        assertTrue(result.isEmpty());
        verify(accountFetcher).fetchAccountBlock(owner, 0, pageable.getSort());
        verify(accountFetcher).fetchAccountBlock(owner, 1, pageable.getSort());
    }

    @Test
    void findAccountsByOwner_singleBlockRequested_notEnoughAccounts() {
        OwnerRequest owner = new OwnerRequest(1L, OwnerType.USER);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));

        AccountPreviewDto acc1 = new AccountPreviewDto(); acc1.setId(1L);

        when(accountFetcher.fetchAccountBlock(owner, 0, pageable.getSort()))
                .thenReturn(Collections.singletonList(acc1));
        when(accountFetcher.fetchAccountBlock(owner, 1, pageable.getSort()))
                .thenReturn(Collections.emptyList());

        List<AccountPreviewDto> result = accountService.findAccountsByOwner(owner, pageable);

        assertEquals(1, result.size());
        assertEquals(acc1, result.get(0));
        verify(accountFetcher).fetchAccountBlock(owner, 0, pageable.getSort());
        verify(accountFetcher).fetchAccountBlock(owner, 1, pageable.getSort());
    }

    @Test
    void findAccountsByOwner_pageOffsetWithinBlock() {
        OwnerRequest owner = new OwnerRequest(1L, OwnerType.USER);
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id"));

        AccountPreviewDto acc1 = new AccountPreviewDto(); acc1.setId(1L);
        AccountPreviewDto acc2 = new AccountPreviewDto(); acc2.setId(2L);

        when(accountFetcher.fetchAccountBlock(owner, 0, pageable.getSort()))
                .thenReturn(Arrays.asList(acc1, acc2));

        List<AccountPreviewDto> result = accountService.findAccountsByOwner(owner, pageable);

        assertEquals(1, result.size());
        assertEquals(acc1, result.get(0));
        verify(accountFetcher).fetchAccountBlock(owner, 0, pageable.getSort());
        verify(accountFetcher, never()).fetchAccountBlock(owner, 1, pageable.getSort());
    }
}
