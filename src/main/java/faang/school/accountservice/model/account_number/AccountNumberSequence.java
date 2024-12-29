package faang.school.accountservice.model.account_number;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumberSequence {

    @EmbeddedId
    private AccountSequenceId id;

    @Column(name = "counter")
    private long counter;
}
