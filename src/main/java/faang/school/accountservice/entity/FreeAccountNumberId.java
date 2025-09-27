package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeAccountNumberId implements Serializable {
    private AccountType accountType;
    private String accountNumber;
}