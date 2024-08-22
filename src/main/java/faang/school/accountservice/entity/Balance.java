package faang.school.accountservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance")
@NoArgsConstructor
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "account_gen", allocationSize = 10)
    private long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}