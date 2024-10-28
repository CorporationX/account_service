package faang.school.accountservice.service.owner;

import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.mapper.owner.OwnerMapper;
import faang.school.accountservice.repository.owner.OwnerRepository;
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
class OwnerServiceTest {

    @InjectMocks
    private OwnerService ownerService;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerMapper ownerMapper;

    @Test
    @DisplayName("When entity exists in db then return it")
    void whenEntityExistsThenNoExceptionThrown() {
        when(ownerRepository.findByName(anyString()))
                .thenReturn(Optional.of(Owner.builder().build()));

        ownerService.getOwnerByName(anyString());

        verify(ownerRepository).findByName(anyString());
    }

    @Test
    @DisplayName("When entity not exists in db then throws exception")
    void whenEntityNotExistsThenExceptionThrown() {
        when(ownerRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> ownerService.getOwnerByName(anyString()));
    }
}