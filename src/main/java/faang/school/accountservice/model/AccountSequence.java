package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OptimisticLocking;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@OptimisticLocking
@Table(name = "account_numbers_sequence")
public class AccountSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type", nullable = false, length = 24, unique = true)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "counter", nullable = false)
    private long counter;

    @Version
    private Long version;
}
