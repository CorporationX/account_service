package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_number_sequence")
public class AccountNumberSequence {

    @Id
    @Column(name = "account_type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "counter", nullable = false)
    private Long counter;

    @Version
    @NotNull
    @Column(name = "version", nullable = false)
    private Long version;
}
