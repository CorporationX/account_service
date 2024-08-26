package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountValidatorTest {
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountValidator accountValidator;
    private AccountDto accountDto;

    @BeforeEach
    void init() {
        accountDto = AccountDto.builder()
                .number("1234567890123")
                .build();
    }

    @Test
    @DisplayName("checkExistenceOfTheNumberFalse")
    void testCheckExistenceOfTheNumberFalse() {
        when(accountRepository.existsByNumber(anyString()))
                .thenReturn(true);
        assertThrows(IllegalArgumentException.class, () ->
                accountValidator.checkExistenceOfTheNumber(accountDto));
    }

    @Test
    @DisplayName("checkExistenceOfTheNumberTrue")
    void testCheckExistenceOfTheNumberTrue() {
        when(accountRepository.existsByNumber(anyString()))
                .thenReturn(false);
        accountValidator.checkExistenceOfTheNumber(accountDto);
    }
}