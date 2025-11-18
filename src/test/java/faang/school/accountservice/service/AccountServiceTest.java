package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserContext userContext;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountService accountService;
    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    private final long userId = 1L;
    private final long accountId = 2L;

    @Test
    void openAccount_shouldCreateAccount() {
        when(userContext.getUserId()).thenReturn(userId);

        AccountCreateDto accountCreateDto = new AccountCreateDto(
                AccountType.INDIVIDUAL_CURRENCY,
                Currency.RUB,
                "description"
        );

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocationOnMock -> {
                    Account saved = invocationOnMock.getArgument(0);
                    saved.setId(accountId);
                    saved.setCreatedAt(LocalDateTime.now());
                    saved.setUpdatedAt(LocalDateTime.now());
                    return saved;
                });

        AccountDto accountDto = accountService.openAccount(accountCreateDto);

        assertEquals(AccountStatus.ACTIVE, accountDto.status());
        assertEquals(accountDto.userId(), userId);
        assertEquals(AccountType.INDIVIDUAL_CURRENCY, accountDto.type());
        assertEquals(Currency.RUB, accountDto.currency());
        assertEquals("description", accountDto.description());
        assertEquals(0, accountDto.balance().compareTo(BigDecimal.ZERO));

        TestUtils.assertTimestamp(accountDto.createdAt());
        TestUtils.assertTimestamp(accountDto.updatedAt());
        TestUtils.validateAccountNumber(accountDto.accountNumber());

        assertNull(accountDto.blockedAt());
        assertNull(accountDto.blockReason());
        assertNull(accountDto.closedAt());
        assertNull(accountDto.closeReason());
        assertNull(accountDto.frozenAt());
        assertNull(accountDto.frozenReason());
    }


}