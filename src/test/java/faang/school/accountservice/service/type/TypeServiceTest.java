package faang.school.accountservice.service.type;

import faang.school.accountservice.entity.type.AccountType;
import faang.school.accountservice.mapper.type.TypeMapper;
import faang.school.accountservice.repository.type.TypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeServiceTest {

    @InjectMocks
    private TypeService typeService;

    @Mock
    private TypeRepository typeRepository;

    @Mock
    private TypeMapper typeMapper;

    @Test
    @DisplayName("When entity exists in db then return it")
    void whenEntityExistsThenNoExceptionThrown() {
        when(typeRepository.findByName(anyString()))
                .thenReturn(Optional.of(AccountType.builder().build()));

        typeService.getTypeByName(anyString());

        verify(typeRepository).findByName(anyString());
    }

    @Test
    @DisplayName("When entity not exists in db then throws exception")
    void whenEntityNotExistsThenExceptionThrown() {
        when(typeRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> typeService.getTypeByName(anyString()));
    }
}