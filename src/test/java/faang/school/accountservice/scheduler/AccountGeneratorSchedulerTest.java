package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.CardType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountGeneratorSchedulerTest {
    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;
    @InjectMocks
    private AccountGeneratorScheduler accountGeneratorScheduler;

    @Test
    public void givenStartScheduler_whenCronExpressionIsProvided_thenCorrect() {
        int batchSize = 10;
        ReflectionTestUtils.setField(accountGeneratorScheduler, "batchSize", batchSize);
        accountGeneratorScheduler.generateAccountNumbers();

        for (CardType cardType : CardType.values()) {
            verify(freeAccountNumbersService, times(1))
                    .generateAccountNumbersForType(cardType, 10);
        }

    }
}
