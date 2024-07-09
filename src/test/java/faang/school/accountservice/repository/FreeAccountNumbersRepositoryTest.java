package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.jpa.FreeAccountNumbersJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumbersRepositoryTest {
    @InjectMocks
    private FreeAccountNumbersRepository repository;
    @Mock
    private FreeAccountNumbersJpaRepository jpaRepository;

    private final ArgumentCaptor<FreeAccountNumber> freeAccountNumberArgumentCaptor =
            ArgumentCaptor.forClass(FreeAccountNumber.class);

    @Test
    void createNewFreeNumberTest() {
        var sequence = AccountNumbersSequence
                .builder()
                .sequence(1L)
                .type(AccountType.CREDIT_ACCOUNT)
                .build();
        var number = String.format("%d%016d",
                sequence.getType().getCode(),
                sequence.getSequence());


        repository.createNewFreeNumber(sequence);

        verify(jpaRepository).save(freeAccountNumberArgumentCaptor.capture());
        FreeAccountNumber capturedValue = freeAccountNumberArgumentCaptor.getValue();
        assertEquals(number, capturedValue.getNumber());
        assertEquals(sequence.getType(), capturedValue.getType());
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void getFreeAccountNumberPositiveTest(AccountType type) {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder().id(1L).build();
        when(jpaRepository.getReferenceByType(type.name())).thenReturn(Optional.of(freeAccountNumber));

        Optional<FreeAccountNumber> result = repository.getFreeAccountNumber(type);

        assertEquals(freeAccountNumber, result.get());
        verify(jpaRepository).deleteById(freeAccountNumber.getId());
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void getFreeAccountNumberNegativeTest(AccountType type) {
        when(jpaRepository.getReferenceByType(type.name())).thenReturn(Optional.empty());

        Optional<FreeAccountNumber> result = repository.getFreeAccountNumber(type);

        assertEquals(Optional.empty(), result);
        verify(jpaRepository, times(0)).deleteById(anyLong());
    }
}