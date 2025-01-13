package faang.school.accountservice.entity;

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
@Table(name = "free_account_sequence")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSeq {

    @Id
    @Column(name = "type",nullable = false,length = 32)
    @Enumerated(value = EnumType.STRING)
    private AccountType type;

    @Column(name = "counter",nullable = false)
    private long counter;
}
