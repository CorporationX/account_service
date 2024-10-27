package faang.school.accountservice.model.entity;


import faang.school.accountservice.model.enums.AccountType;
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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "account_numbers_sequence")
public class AccountNumberSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "counter", nullable = false)
    @ColumnDefault(value = "0")
    private Long counter;

    @Column(name = "account_type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "version", nullable = false)
    @Version
    @ColumnDefault(value = "1")
    private Long version;
}
