package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumberServiceTest {
    @InjectMocks
    private FreeAccountNumberService freeAccountNumberService;
    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Test
    void whenGetNextSavingsAccountNumberThenReturnAccountNumber() {
        // given - precondition
        var expectedResult = TestDataFactory.ACCOUNT_NUMBER;
        when(freeAccountNumbersRepository.getSavingsAccountNumber(AccountType.SAVINGSACCOUNT)).thenReturn(expectedResult);
        doNothing().when(freeAccountNumbersRepository).deleteSavingsAccountNumber(expectedResult);

        // when - action
        var actualResult = freeAccountNumberService.getNextSavingsAccountNumber();

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}