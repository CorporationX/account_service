package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_number_sequence")
@Data
@NoArgsConstructor
public class AccountSeq {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "accounttype", nullable = false, length = 32)
    private AccountType accountType;

    @Column(name = "counter", nullable = false)
    private long counter;

    @Transient
    private long initialValue;
}
