package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumbers;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.InvalidAccountNumberException;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumberPoolServiceTest {

    @Mock
    private FreeAccountNumbersRepository repository;

    @Mock
    private AccountNumberValidator validator;

    @InjectMocks
    private FreeAccountNumberPoolService poolService;

    @Test
    @DisplayName("Should add valid number to pool")
    void shouldAddValidNumberToPool() {
        AccountType type = AccountType.DEBIT;
        String number = "420000000001";
        FreeAccountNumbers expected = new FreeAccountNumbers(type, number);

        doNothing().when(validator).validate(type, number);
        when(repository.createFreeAccountNumber(type, number)).thenReturn(expected);

        FreeAccountNumbers result = poolService.addToPool(type, number);

        assertThat(result).isEqualTo(expected);
        verify(validator).validate(type, number);
    }

    @Test
    @DisplayName("Should throw exception for duplicate number")
    void shouldThrowExceptionForDuplicateNumber() {
        AccountType type = AccountType.CREDIT;
        String number = "550000000001";

        doNothing().when(validator).validate(type, number);
        when(repository.createFreeAccountNumber(type, number))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThatThrownBy(() -> poolService.addToPool(type, number))
                .isInstanceOf(InvalidAccountNumberException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should retrieve number from pool")
    void shouldRetrieveNumberFromPool() {
        AccountType type = AccountType.SAVINGS;
        String number = "430000000001";

        when(repository.findAndDeleteFirstAvailableNumber("SAVINGS"))
                .thenReturn(Optional.of(number));
        when(validator.isValid(type, number)).thenReturn(true);

        Optional<String> result = poolService.retrieveFromPool(type);

        assertThat(result).isPresent().contains(number);
    }

    @Test
    @DisplayName("Should filter out invalid numbers from pool")
    void shouldFilterOutInvalidNumbersFromPool() {
        AccountType type = AccountType.BUSINESS;
        String invalidNumber = "invalid";

        when(repository.findAndDeleteFirstAvailableNumber("BUSINESS"))
                .thenReturn(Optional.of(invalidNumber));
        when(validator.isValid(type, invalidNumber)).thenReturn(false);

        Optional<String> result = poolService.retrieveFromPool(type);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should count available numbers correctly")
    void shouldCountAvailableNumbersCorrectly() {
        AccountType type = AccountType.DEBIT;
        long expectedCount = 42L;

        when(repository.countByAccountType(type)).thenReturn(expectedCount);

        long result = poolService.countAvailable(type);

        assertThat(result).isEqualTo(expectedCount);
    }
}