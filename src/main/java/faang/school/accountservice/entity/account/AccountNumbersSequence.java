package faang.school.accountservice.entity.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumbersSequence {

    @Id
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "counter", nullable = false)
    private long counter;

    @Transient
    private long initialValue;
    
}
