package faang.school.accountservice.validation;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.ForbiddenAccessException;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.service.account_owner.AccountOwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountOwnerValidatorTest {
    private final static long USER_ID = 1L;

    private AccountOwner accountOwner;

    @Mock
    private AccountOwnerService accountOwnerService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private AccountOwnerValidator accountOwnerValidator;

    @BeforeEach
    public void setUp() {
        accountOwner = AccountOwner.builder()
                .ownerId(2L)
                .ownerType(OwnerType.USER)
                .build();
    }

    @Test
    public void testValidateOwnerByOwnerIdAndTypeUserInvalid() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        assertThrows(ForbiddenAccessException.class, () -> accountOwnerValidator.validateOwnerByOwnerIdAndType(2L, OwnerType.USER));
    }

    @Test
    public void testValidateOwnerUserInvalid() {
        when(userContext.getUserId()).thenReturn(USER_ID);

        assertThrows(ForbiddenAccessException.class, () -> accountOwnerValidator.validateOwner(accountOwner));
    }

    @Test
    public void testValidatOwnerByAccountIdUserInvalid() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(accountOwnerService.getAccountOwnerById(2L)).thenReturn(accountOwner);
        assertThrows(ForbiddenAccessException.class, () -> accountOwnerValidator.validateOwnerByAccountOwnerId(2L));
    }
}
