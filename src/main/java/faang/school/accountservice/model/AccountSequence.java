package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_number_sequence")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSequence {
    @Id
    @Column(name = "type", nullable = false, unique = true, length = 32)
    private String type;
    @Column(name = "counter", nullable = false)
    private long counter;
    @Transient
    private long initialValue;
}
