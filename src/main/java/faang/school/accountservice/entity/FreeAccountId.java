package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Embeddable
@RequiredArgsConstructor
public class FreeAccountId {
    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(value = EnumType.STRING)
    private final AccountType type;

    @Column(name = "account_number", nullable = false)
    private final long accountNumber;

    public FreeAccountId() {
        this.type = null;
        this.accountNumber = 0;
    }
}
