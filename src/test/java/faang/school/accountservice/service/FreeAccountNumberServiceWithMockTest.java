package faang.school.accountservice.service;

import faang.school.accountservice.config.TestContainersConfig;
import faang.school.accountservice.exception.ResourceNotAvailableException;
import faang.school.accountservice.mapper.FreeAccountNumberMapper;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static faang.school.accountservice.enums.account.AccountType.SAVINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@EnableRetry
@Testcontainers
@MockBean(AccountNumbersSequenceRepository.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestContainersConfig.class)
class FreeAccountNumberServiceWithMockTest extends TestContainersConfig {
    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Autowired
    private FreeAccountNumberMapper freeAccountNumberMapper;

    @AfterEach
    public void cleanUp() {
        freeAccountNumbersRepository.deleteAll();
        accountNumbersSequenceRepository.deleteAll();
    }

    @Test
    void testAccountTypeNotFound() {
        when(accountNumbersSequenceRepository.incrementCounter(SAVINGS.getCode())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> freeAccountNumberService.createNextNumber(SAVINGS, true));
    }

    @Test
    void testCreateNextNumber_Retry5Times() {
        when(accountNumbersSequenceRepository.incrementCounter(SAVINGS.getCode()))
                .thenThrow(new PersistenceException())
                .thenThrow(new PersistenceException())
                .thenThrow(new PersistenceException())
                .thenThrow(new PersistenceException())
                .thenThrow(new PersistenceException());

        assertThrows(ResourceNotAvailableException.class,
                () -> freeAccountNumberService.createNextNumber(SAVINGS, true));
        verify(accountNumbersSequenceRepository, times(5)).incrementCounter(SAVINGS.getCode());
    }

    @Test
    void testCreateNextNumber_Retry4TimesAndSuccessReturn() {
        AccountNumbersSequence sequence = AccountNumbersSequence.builder()
                .accountType(SAVINGS.getCode())
                .currentNumber(1L)
                .build();
        String number = "5536000000000000";
        FreeAccountNumber expectedNumber = FreeAccountNumber.builder()
                .accountNumber(number)
                .accountType(SAVINGS.getCode())
                .build();

        when(accountNumbersSequenceRepository.incrementCounter(SAVINGS.getCode()))
                .thenThrow(new PersistenceException())
                .thenThrow(new PersistenceException())
                .thenThrow(new PersistenceException())
                .thenThrow(new PersistenceException())
                .thenReturn(Optional.of(sequence));

        FreeAccountNumber result = freeAccountNumberService.createNextNumber(SAVINGS, true);

        assertEquals(expectedNumber, result);
        verify(accountNumbersSequenceRepository, times(5)).incrementCounter(SAVINGS.getCode());
    }
}