package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Embeddable
@AllArgsConstructor
@Data
public class AccountNumberId {
    @Column(name = "number", nullable = false, unique = false)
    private long number;
    @Column(name = "type", nullable = false, unique = false, length = 32)
    private String type;
}
