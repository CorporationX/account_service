package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "free_account_sequence")
@Data
@NoArgsConstructor
public class AccountSeq {

    @Id
    @Column(name = "type",nullable = false,length = 32)
    private AccountType type;

    @Column(name = "counter",nullable = false)
    private long counter;

    @Transient
    private long initialValue;
}
