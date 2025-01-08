package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
@Entity
public class Account {

    @Id
    private long userOwnerId;

    @Column(name = "number", length = 20, nullable = false, unique = true)
    private long number;

    @Column(name = "owner_account", nullable = false)
    @Enumerated(EnumType.STRING)
    private Owner ownerAccount;

    @Column(name = "type", length = 128, nullable = false)
    private AccountType type;

    @Column(name = "currency", length = 8, nullable = false)
    private String currency;

    @Column(name = "version", nullable = false)
    private long version;
}