package faang.school.accountservice.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_numbers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class FreeAccountNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type;

    @Column(name = "number", length = 20, nullable = false, unique = true)
    private String number;

    @Version
    @Column(name = "version")
    private Integer version;
}
