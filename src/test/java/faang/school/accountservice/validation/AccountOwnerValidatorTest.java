package faang.school.accountservice.validation;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.project.ProjectDto;
import faang.school.accountservice.dto.project.TeamDto;
import faang.school.accountservice.dto.project.TeamMemberDto;
import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.project.TeamRole;
import faang.school.accountservice.exception.ForbiddenAccessException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.service.account_owner.AccountOwnerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountOwnerValidatorTest {
    private final static long USER_ID = 1L;
    private final static long PROJECT_ID = 1L;

    private AccountOwner accountOwnerUser;
    private AccountOwner accountOwnerProject;
    private UserDto user;

    @Mock
    private AccountOwnerService accountOwnerService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private AccountOwnerValidator accountOwnerValidator;

    @BeforeEach
    public void setUp() {
        accountOwnerUser = AccountOwner.builder()
                .ownerId(2L)
                .ownerType(OwnerType.USER)
                .build();
        accountOwnerProject = AccountOwner.builder()
                .ownerId(PROJECT_ID)
                .ownerType(OwnerType.PROJECT)
                .build();
        user = new UserDto(1L);
    }

    @Test
    public void testValidateOwnerByOwnerIdAndTypeUserInvalid() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(user);

        assertThrows(ForbiddenAccessException.class, () -> accountOwnerValidator.validateOwnerByOwnerIdAndType(2L, OwnerType.USER));

        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    public void testValidateOwnerUserInvalid() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(user);

        assertThrows(ForbiddenAccessException.class, () -> accountOwnerValidator.validateOwner(accountOwnerUser));

        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    public void testValidateOwnerByAccountIdUserInvalid() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(user);
        when(accountOwnerService.getAccountOwnerById(2L)).thenReturn(accountOwnerUser);

        assertThrows(ForbiddenAccessException.class, () -> accountOwnerValidator.validateOwnerByAccountOwnerId(2L));

        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
        verify(accountOwnerService).getAccountOwnerById(2L);
    }

    @Test
    public void testValidateOwnerUserNotExists() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> accountOwnerValidator.validateOwnerByOwnerIdAndType(USER_ID, OwnerType.USER));

        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    public void testValidateOwnerProjectNotOwnerOrManager() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(anyLong())).thenReturn(user);
        when(projectServiceClient.getProjectById(1L)).thenReturn(prepareProject());

        assertThrows(ForbiddenAccessException.class, () -> accountOwnerValidator.validateOwnerByOwnerIdAndType(USER_ID, OwnerType.PROJECT));

        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
        verify(projectServiceClient).getProjectById(1L);
    }

    private ProjectDto prepareProject() {
        TeamMemberDto teamMemberDto = new TeamMemberDto(1L, 2L, Set.of(TeamRole.OWNER));
        TeamMemberDto teamMemberDto1 = new TeamMemberDto(2L, 3L, Set.of(TeamRole.MANAGER));
        TeamMemberDto teamMemberDto2 = new TeamMemberDto(3L, USER_ID, Set.of(TeamRole.DEVELOPER));
        TeamDto team = new TeamDto(1L, List.of(teamMemberDto, teamMemberDto1, teamMemberDto2));
        return new ProjectDto(PROJECT_ID, List.of(team));
    }

}
