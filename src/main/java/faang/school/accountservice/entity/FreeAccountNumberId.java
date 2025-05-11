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

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeAccountNumberId implements Serializable {

    @Column(name = "type", nullable = false, length = 16)
    @Enumerated(value = EnumType.STRING)
    private AccountNumberType type;

    @Column(name = "account_number", nullable = false)
    private long accountNumber;
}
