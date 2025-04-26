package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "account_number_sequans")
public class AccountSeq {
    @Id
    @Column(name = "type", nullable = false, length = 32)
    private AccountType accountType;
    @Column(name = "counter",nullable = false)
    private Long counter;
    @Transient
    private long initialValue;
}
