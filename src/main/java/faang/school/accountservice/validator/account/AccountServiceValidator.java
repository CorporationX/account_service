package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.CreateAccountDto;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceValidator {
    public void checkId(long... id) {
        for (int i = 0; i < id.length; i++) {
            if (id[i] < 0) {
                throw new IllegalArgumentException(String.format("Id must be > 0 , illegal %d", id[i]));
            }
        }
    }

    public void validateCreateAccountDto(CreateAccountDto createAccountDto) {
        if (createAccountDto == null) {
            throw new IllegalArgumentException("CreateAccountDto cant be null");
        }

        if (createAccountDto.getType() == null || createAccountDto.getCurrency() == null || createAccountDto.getOwnerType() == null) {
            throw new IllegalArgumentException("Type, Currency and Owner must not be null");
        }

    }
}
