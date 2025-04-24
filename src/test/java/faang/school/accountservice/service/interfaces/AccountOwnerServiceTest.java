package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountOwnerRepository;
import faang.school.accountservice.service.implementations.AccountOwnerServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountOwnerServiceTest {

    @Mock
    private AccountOwnerRepository accountOwnerRepository;

    @InjectMocks
    private AccountOwnerServiceImpl accountOwnerService;

    private AccountOwner accountOwner;
    private final Long ownerId = 123L;
    private final OwnerType ownerType = OwnerType.USER;

    @BeforeEach
    void setUp() {
        accountOwner = new AccountOwner();
        accountOwner.setId(ownerId);
        accountOwner.setOwnerType(ownerType);
    }

    @Test
    void testFindOwner_Success() {
        when(accountOwnerRepository.findByOwnerIdAndOwnerType(ownerId, ownerType))
                .thenReturn(Optional.of(accountOwner));

        AccountOwner result = accountOwnerService.findOwner(ownerId, ownerType);

        assertNotNull(result);
        assertEquals(ownerId, result.getId());
        assertEquals(ownerType, result.getOwnerType());
        verify(accountOwnerRepository, times(1))
                .findByOwnerIdAndOwnerType(ownerId, ownerType);
    }

    @Test
    void testFindOwner_NotFound_ThrowsEntityNotFoundException() {
        when(accountOwnerRepository.findByOwnerIdAndOwnerType(ownerId, ownerType))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accountOwnerService.findOwner(ownerId, ownerType)
        );

        assertEquals("Owner with id: " + ownerId + " not found", exception.getMessage());
        verify(accountOwnerRepository, times(1))
                .findByOwnerIdAndOwnerType(ownerId, ownerType);
    }
}