package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "account_numbers_sequence")
public class AccountSeq {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private AccountType accountType;
    @Column(name = "counter",nullable = false)
    private Long counter;
    @Transient
    private long initialValue;
}
