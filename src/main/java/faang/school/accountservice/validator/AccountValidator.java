package faang.school.accountservice.validator;

import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountValidator {

    public void validateAccountOwner(OwnerType ownerType, Long ownerProjectId, Long ownerUserId) {

        if (ownerProjectId == null && ownerUserId == null) {
            log.error("Owner project_id and Owner user_id cannot be both null");
            throw new DataValidationException("Owner project_id and Owner user_id cannot be both null");
        }

        if (ownerType.equals(OwnerType.PROJECT)) {
            if (ownerUserId != null && ownerProjectId == null) {
                log.error("Owner project ID is null, owner user id is not null - Owner Type mismatch. Owner type - Project");
                throw new DataValidationException("Owner project ID is null, owner user id is not null - Owner Type mismatch. Owner type - Project");
            }
        } else if (ownerType.equals(OwnerType.USER)) {
            if (ownerProjectId != null && ownerUserId == null) {
                log.error("Owner project ID is null, owner user id is not null - Owner Type mismatch. Owner type - User");
                throw new DataValidationException("Owner project ID is null, owner user id is not null - Owner Type mismatch. Owner type - User");
            }
        }
    }
}
