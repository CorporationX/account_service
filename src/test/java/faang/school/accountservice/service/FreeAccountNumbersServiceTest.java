package faang.school.accountservice.service;

import faang.school.accountservice.dto.FreeAccountDto;
import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.CardType;
import faang.school.accountservice.exception.InvalidBatchSizeException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FreeAccountNumbersServiceTest {
    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @BeforeEach
    public void init() {
        freeAccountNumbersRepository.deleteAll();
        accountNumbersSequenceRepository.deleteAll();
    }

    @Test
    public void givenSpringContext_whenApplicationStart_thenCardSInitialized() {
        freeAccountNumbersService.initCards();

        for (CardType cardType : CardType.values()) {
            assertTrue(accountNumbersSequenceRepository.existsAccountNumberSequenceByType(cardType));
        }
    }

    @Test
    public void givenCardType_whenGenerateAccountNumbersForType_thenThrow() {
        assertThrows(InvalidBatchSizeException.class,
                () -> freeAccountNumbersService.generateAccountNumbersForType(CardType.CREDIT, -1));
    }

    @Test
    public void givenCardType_whenGenerateAccountNumbersForType_thenGenerated() {
        freeAccountNumbersService.initCards();
        freeAccountNumbersService.generateAccountNumbersForType(CardType.CREDIT, 10);

        List<AccountNumberSequence> actualAccountNumberSequences = accountNumbersSequenceRepository.findAll();
        assertNotNull(actualAccountNumberSequences);
        assertFalse(actualAccountNumberSequences.isEmpty());
        assertTrue(actualAccountNumberSequences.stream()
                .anyMatch(accountNumberSequence ->
                        accountNumberSequence.getType().equals(CardType.CREDIT)
                                && accountNumberSequence.getCount() == 10));

        List<FreeAccountNumber> actualAccountNumberList = freeAccountNumbersRepository.findAll();
        assertNotNull(actualAccountNumberList);
        assertFalse(actualAccountNumberList.isEmpty());
        assertEquals(10, actualAccountNumberList.size());
    }

    @Test
    public void givenCardType_whenGetFreeAccountForType_thenGetCardWhenCardsEmpty() {
        CardType creditCardType = CardType.CREDIT;
        freeAccountNumbersService.initCards();
        freeAccountNumbersService.generateAccountNumbersForType(creditCardType, 1);
        freeAccountNumbersService.getFreeAccountForType(creditCardType, this::toFreeAccountDto);

        FreeAccountDto actualFreeAccountDto = freeAccountNumbersService.getFreeAccountForType(creditCardType,
                freeAccountNumber -> {
                    FreeAccountNumberId numberId = freeAccountNumber.getAccountId();
                    return new FreeAccountDto(numberId.getCardType().name(), numberId.getCardNumber());
                });

        assertNotNull(actualFreeAccountDto);
        assertEquals(creditCardType.name(), actualFreeAccountDto.getType());
        assertEquals(creditCardType.getCardPattern() + 1, actualFreeAccountDto.getNumber());
    }

    @Test
    public void givenCardType_whenGetFreeAccountForType_thenGetCardWithNotEmpty() {
        CardType creditCardType = CardType.CREDIT;
        freeAccountNumbersService.initCards();
        freeAccountNumbersService.generateAccountNumbersForType(creditCardType, 1);

        FreeAccountDto actualFreeAccountDto = freeAccountNumbersService.getFreeAccountForType(
                creditCardType, this::toFreeAccountDto);

        assertNotNull(actualFreeAccountDto);
        assertEquals(creditCardType.name(), actualFreeAccountDto.getType());
        assertEquals(creditCardType.getCardPattern(), actualFreeAccountDto.getNumber());
    }

    private FreeAccountDto toFreeAccountDto(FreeAccountNumber freeAccountNumber) {
        FreeAccountNumberId numberId = freeAccountNumber.getAccountId();
        return new FreeAccountDto(numberId.getCardType().name(), numberId.getCardNumber());
    }
}
