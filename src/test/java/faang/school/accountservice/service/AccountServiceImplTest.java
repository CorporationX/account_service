package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.mapper.account.AccountMapperImpl;
import faang.school.accountservice.properties.AccountProperties;
import faang.school.accountservice.repository.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private AccountMapperImpl accountMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private AccountProperties accountProperties;

    @Mock
    private AccountProperties.Number numberProperties;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        account = getAccount();
        accountDto = getAccountDto();
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUserById(eq(1L))).thenReturn(UserDto.builder().id(1L).build());
    }

    @Test
    void testGetAccountById() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountDto result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("123456789012", result.number());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOwnerAccounts() {
        when(accountRepository.findAllByOwnerIdAndOwnerType(1L, "USER")).thenReturn(List.of(account));

        List<AccountDto> result = accountService.getOwnerAccounts(1L, "USER");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(accountRepository).findAllByOwnerIdAndOwnerType(1L, "USER");
        verify(accountMapper).toDto(account);
    }

    @Test
    void testCreateAccount() {
        when(accountProperties.getNumber()).thenReturn(numberProperties);
        when(numberProperties.getMinDigits()).thenReturn(12);
        when(numberProperties.getMaxDigits()).thenReturn(20);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDto result = accountService.createAccount(accountDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("123456789012", result.number());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testBlockAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.blockAccount(1L);

        assertEquals(AccountStatus.FROZEN, account.getStatus());
        assertNotNull(account.getUpdatedAt());
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(account);
    }

    @Test
    void closeAccount_ShouldCloseAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.closeAccount(1L);

        assertEquals(AccountStatus.CLOSED, account.getStatus());
        assertNotNull(account.getUpdatedAt());
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(account);
    }

    private Account getAccount() {
        return Account.builder()
                .id(1L)
                .number(new BigInteger("123456789012"))
                .ownerId(1L)
                .ownerType(OwnerType.USER)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private AccountDto getAccountDto() {
        return AccountDto.builder()
                .id(1L)
                .number("123456789012")
                .ownerId(1L)
                .ownerType(OwnerType.USER)
                .status(AccountStatus.ACTIVE)
                .build();
    }

}