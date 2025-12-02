package faang.school.accountservice.entity.account;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_number_sequence")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSeq {

    @Id
    @Enumerated(EnumType.STRING)

    @Column(name = "type", nullable = false, length = 16)
    private AccountType type;

    @Column(name = "counter", nullable = false)
    private long counter;
}