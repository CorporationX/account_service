package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="account_number_sequence")
public class AccountSeq{

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable=false, length=32)
    private AccountType type;

    @Column(name="counter", nullable=false)
    private long counter;

    @Transient
    private long initialValue;

}
