package faang.school.accountservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "account")
@EqualsAndHashCode(of = "id")
public class Account {
    @Id
    private Long id;

    @OneToOne(mappedBy = "account")
    private Balance balance;
}
