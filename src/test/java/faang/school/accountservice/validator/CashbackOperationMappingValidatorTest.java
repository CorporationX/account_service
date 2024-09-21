package faang.school.accountservice.validator;

import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashbackOperationMappingValidatorTest {

    @Mock
    CashbackOperationMappingRepository cashbackOperationMappingRepository;

    @InjectMocks
    CashbackOperationMappingValidator cashbackOperationMappingValidator;

    private final Long cashbackOperationMappingId = 1L;

    @Test
    @DisplayName("When validating exists mapping, and it does not exist, then throw EntityNotFoundException")
    void validateExists_MappingDoesNotExist_ThrowsException() {
        when(cashbackOperationMappingRepository.existsById(cashbackOperationMappingId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
            () -> cashbackOperationMappingValidator.validateExists(cashbackOperationMappingId));
        verify(cashbackOperationMappingRepository).existsById(cashbackOperationMappingId);
    }

    @Test
    @DisplayName("When validating exists mapping, and it exists, then do nothing")
    void validateExists_MappingExists_DoesNothing() {
        when(cashbackOperationMappingRepository.existsById(cashbackOperationMappingId)).thenReturn(true);

        cashbackOperationMappingValidator.validateExists(cashbackOperationMappingId);

        verify(cashbackOperationMappingRepository).existsById(cashbackOperationMappingId);
    }

}