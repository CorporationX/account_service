package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    private static final String CODE_MESSAGE_ERROR = "message.error.accountNotFound";
    private static final String VALID_NUMBER = "4200000000000001";
    private static final String INVALID_NUMBER = "123";
    private Account account;
    private AccountDto accountDto;
    @Mock
    private AccountRepository repository;
    @Mock
    private AccountMapperImpl mapper;
    @Spy
    private MessageSource messageSource;
    @InjectMocks
    private AccountService service;

    @BeforeEach
    void setUp() {
        //Arrange
        account = new Account();
        accountDto = new AccountDto();
    }

    @Test
    void testValidGetAccount() {
        //Act
        when(repository.findAccountByNumber(VALID_NUMBER)).thenReturn(Optional.of(account));
        when(mapper.toDto(account)).thenReturn(accountDto);
        service.getAccount(VALID_NUMBER);
        //Assert
        verify(mapper).toDto(account);
    }

    @Test
    void testInvalidGetAccount() {
        //Act
        when(repository.findAccountByNumber(INVALID_NUMBER)).thenReturn(Optional.empty());
        //Assert
        assertEquals(messageSource.getMessage(CODE_MESSAGE_ERROR, null, LocaleContextHolder.getLocale()),
                assertThrows(RuntimeException.class, () -> service.getAccount(INVALID_NUMBER)).getMessage());
    }

    @Test
    void testOpenAccount() {
        //Act
        when(mapper.toEntity(accountDto)).thenReturn(account);
        service.openAccount(accountDto);
        //Assert
        assertEquals(account.getStatus(), Status.ACTIVE);
        verify(repository).save(account);
        verify(mapper).toDto(account);
    }

    @Test
    void testUpdateAccountStatusOnBlocked() {
        //Act
        when(repository.findAccountByNumber(VALID_NUMBER)).thenReturn(Optional.of(account));
        when(mapper.toDto(account)).thenReturn(accountDto);
        when(mapper.toEntity(accountDto)).thenReturn(account);
        service.updateAccountStatus(VALID_NUMBER, Status.BLOCKED.toString());
        //Assert
        assertEquals(account.getStatus(), Status.BLOCKED);
        assertNull(account.getClosedAt());
        verify(repository).save(account);
    }

    @Test
    void testUpdateAccountStatusOnClosed() {
        //Act
        when(repository.findAccountByNumber(VALID_NUMBER)).thenReturn(Optional.of(account));
        when(mapper.toDto(account)).thenReturn(accountDto);
        when(mapper.toEntity(accountDto)).thenReturn(account);
        service.updateAccountStatus(VALID_NUMBER, Status.CLOSED.toString());
        //Assert
        assertEquals(account.getStatus(), Status.CLOSED);
        assertNotNull(account.getClosedAt());
        verify(repository).save(account);
    }
}