package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumbersServiceTest {
    @InjectMocks
    private FreeAccountNumbersService freeAccountNumbersService;
    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;


    @BeforeEach
    void setUp() {

    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void createNewAccountNumberTest(AccountType type) {
        AccountNumbersSequence numbersSequence = new AccountNumbersSequence();
        when(accountNumbersSequenceRepository.findOrCreateByType(type)).thenReturn(numbersSequence);

        freeAccountNumbersService.createNewAccountNumber(type);

        verify(accountNumbersSequenceRepository).incrementAccountNumbersSequence(numbersSequence);
        verify(freeAccountNumbersRepository).createNewFreeNumber(numbersSequence);
    }

    @DisplayName("should consume free number for given type when such number exists")
    @ParameterizedTest
    @EnumSource(AccountType.class)
    void consumeFreeNumberPositiveTest(AccountType type) {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder().number("1234").build();
        Consumer<String> numberConsumer = mock(Consumer.class);

        when(freeAccountNumbersRepository.getFreeAccountNumber(type)).thenReturn(Optional.of(freeAccountNumber));

        freeAccountNumbersService.consumeFreeNumber(type, numberConsumer);

        verify(numberConsumer).accept(anyString());
        verifyNoInteractions(accountNumbersSequenceRepository);
        verify(freeAccountNumbersRepository, times(0))
                .createNewFreeNumber(any(AccountNumbersSequence.class));
    }

    @DisplayName("should create new free number and consume it when the number for given type doesn't exist")
    @ParameterizedTest
    @EnumSource(AccountType.class)
    void consumeFreeNumberNegativeTest(AccountType type) {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder().number("1234").build();
        AccountNumbersSequence numbersSequence = new AccountNumbersSequence();
        when(accountNumbersSequenceRepository.findOrCreateByType(type)).thenReturn(numbersSequence);
        Consumer<String> numberConsumer = mock(Consumer.class);

        when(freeAccountNumbersRepository.getFreeAccountNumber(type))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(freeAccountNumber));

        freeAccountNumbersService.consumeFreeNumber(type, numberConsumer);

        verify(accountNumbersSequenceRepository).incrementAccountNumbersSequence(numbersSequence);
        verify(freeAccountNumbersRepository).createNewFreeNumber(numbersSequence);
        verify(numberConsumer).accept(anyString());
    }
}