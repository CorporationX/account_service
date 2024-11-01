package faang.school.accountservice.validation;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.ForbiddenAccessException;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.service.account_owner.AccountOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountOwnerValidator {
    private final AccountOwnerService accountOwnerService;
    private final UserContext userContext;

    public void validateOwnerByOwnerIdAndType(Long ownerId, OwnerType ownerType) {
        Long userId = userContext.getUserId();
        //todo: здесь нужна интеграция с user_service, чтобы удостовериться, что пользователь есть в системе
        switch (ownerType) {
            case USER -> {
                if (!ownerId.equals(userId)) {
                    throw new ForbiddenAccessException("User %d has no appropriate privileges".formatted(userId));
                }
            }
            case PROJECT -> {
                //todo: здесь нужна интеграция с project_service, чтобы удостовериться, что пользователь участник проекта и узнать его роли
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
