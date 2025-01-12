package faang.school.accountservice.entity;

import faang.school.accountservice.enums.Owner;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column(name = "user_owner_id", length = 20, unique = true)
    private Long userOwnerId;

    @Column(name = "project_owner_id", length = 20, unique = true)
    private Long projectOwnerId;

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