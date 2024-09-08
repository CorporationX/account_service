package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccTypeFreeNumberId {
    @Column(name = "account_type", nullable = false, length = 32)
    @Enumerated(value = EnumType.STRING)
    private AccountType accTypeFreeNumber;

    @Column(name = "free_number", nullable = false)
    private long freeNumber;
}
