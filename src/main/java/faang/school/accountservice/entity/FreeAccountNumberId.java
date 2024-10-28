package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountNumberType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FreeAccountNumberId implements Serializable {

    @Column(name = "type", nullable = false, length = 128)
    @Enumerated(EnumType.STRING)
    private AccountNumberType type;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;
}
