package faang.school.accountservice.validator;

import faang.school.accountservice.model.dto.AccountDto;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class AccountControllerValidator {
    public void checkDto(AccountDto dto) {
        if (dto == null || (dto.getUserId() == null && dto.getProject_id() == null) ||
        (dto.getUserId() != null && dto.getProject_id() != null)) {
            throw new ValidationException("User and project required. They cannot be present at the same time.");
        }
    }
}
