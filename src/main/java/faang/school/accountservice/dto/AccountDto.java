package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Validated
@Getter
@Setter
public class AccountDto {
    private Long id;

    @NotBlank(message = "Account number can't be empty.")
    private String accountNumber;

    @NotBlank(message = "Owner id can't be empty.")
    private BigInteger ownerId;

    @NotBlank(message = "Owner type can't be empty.")
    private AccountOwnerType ownerType;

    @NotBlank(message = "Account type can't be empty.")
    private AccountType type;

    @NotBlank(message = "Currency type can't be empty.")
    private Currency currency;

    private AccountStatus accountStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;
}
