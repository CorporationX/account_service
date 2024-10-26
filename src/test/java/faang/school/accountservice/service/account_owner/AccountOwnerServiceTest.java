package faang.school.accountservice.service.account_owner;

import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.repository.AccountOwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AccountOwnerServiceTest {
    private final static long OWNER_ID = 1L;

    @Mock
    private AccountOwnerRepository accountOwnerRepository;

    @InjectMocks
    private AccountOwnerServiceImpl accountOwnerService;

    private AccountOwner accountOwner;

    @BeforeEach
    public void setUp() {
        accountOwner = AccountOwner.builder()
                .ownerId(OWNER_ID)
                .ownerType(OwnerType.USER)
                .build();
    }

    @Test
    public void testGetOwnerById() {
        when(accountOwnerRepository.findById(1L)).thenReturn(Optional.of(accountOwner));

        AccountOwner result = accountOwnerService.getAccountOwnerById(OWNER_ID);
        assertEquals(accountOwner.getOwnerId(), result.getOwnerId());
        assertEquals(accountOwner.getOwnerType(), result.getOwnerType());

        verify(accountOwnerRepository).findById(OWNER_ID);
    }

    @Test
    public void testGetNonExistingOwnerById() {
        when(accountOwnerRepository.findById(OWNER_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountOwnerService.getAccountOwnerById(OWNER_ID));
    }
}
