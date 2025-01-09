package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class FreeAccountNumberServiceIT extends BaseContextTest {

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @Autowired
    private AccountNumberSequenceRepository accountNumberSequenceRepository;

    @Autowired
    private FreeAccountNumberRepository freeAccountNumberRepository;

    private AccountType accountType;

    @BeforeEach
    void setUp() {
        accountType = AccountType.CURRENT;
        AccountNumberSequence sequence = AccountNumberSequence.builder()
                .type(accountType)
                .counter(100L)
                .build();
        accountNumberSequenceRepository.save(sequence);
    }

    @Test
    void testGenerateFreeAccountNumberSuccess() {
        List<FreeAccountNumber> numbers = freeAccountNumbersService.generateFreeAccountNumber(accountType, 5);

        assertThat(numbers).hasSize(5);

        long firstNumber = numbers.get(0).getId().getAccountNumber();
        for (int i = 0; i < 5; i++) {
            assertThat(numbers.get(i).getId().getType()).isEqualTo(accountType);
            assertThat(numbers.get(i).getId().getAccountNumber()).isEqualTo(firstNumber + i);
        }
    }

    @Test
    void testProcessAndDeleteFreeAccNumberSuccess() {
        freeAccountNumbersService.generateFreeAccountNumber(accountType, 1);

        freeAccountNumbersService.processAndDeleteFreeAccNumber(accountType,
                freeAccountNumber ->
                        assertThat(freeAccountNumber.getId().getType()).isEqualTo(accountType));

        assertThat(freeAccountNumberRepository.findFirstByIdType(accountType)).isEqualTo(null);
    }
}
