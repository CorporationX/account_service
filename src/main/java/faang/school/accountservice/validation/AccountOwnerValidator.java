package faang.school.accountservice.validation;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.project.ProjectDto;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.project.TeamRole;
import faang.school.accountservice.exception.ForbiddenAccessException;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.service.account_owner.AccountOwnerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AccountOwnerValidator {
    private final AccountOwnerService accountOwnerService;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    public void validateOwnerByOwnerIdAndType(Long ownerId, OwnerType ownerType) {
        Long userId = userContext.getUserId();
        if (userServiceClient.getUser(userId) == null) {
            throw new EntityNotFoundException("User %d does not exists".formatted(userId));
        }
        switch (ownerType) {
            case USER -> {
                if (!ownerId.equals(userId)) {
                    throw new ForbiddenAccessException("User %d has no appropriate privileges".formatted(userId));
                }
            }
            case PROJECT -> {
                ProjectDto project = projectServiceClient.getProjectById(ownerId);
                Set<TeamRole> userRoles = new HashSet<>();
                project.teams()
                        .forEach(team -> team.teamMembers().stream()
                                .filter(tm -> userId.equals(tm.userId()))
                                .forEach(tm -> userRoles.addAll(tm.roles()))
                        );
                if (userRoles.contains(TeamRole.MANAGER) || userRoles.contains(TeamRole.OWNER)) {
                    return;
                }
                throw new ForbiddenAccessException("User %d has no appropriate privileges".formatted(userId));
            }
        }
    }

    public void validateOwner(AccountOwner accountOwner) {
        validateOwnerByOwnerIdAndType(accountOwner.getOwnerId(), accountOwner.getOwnerType());
    }

    public void validateOwnerByAccountOwnerId(Long accountOwnerId) {
        AccountOwner accountOwner = accountOwnerService.getAccountOwnerById(accountOwnerId);
        validateOwner(accountOwner);
    }
}
