package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "account_number_sequence")
public class AccountNumbersSequence {


    @Id
    @Column(nullable = false, name = "account_type")
    private String accountType;

    @Column(nullable = false)
    private Long current;

    @Version
    private Integer version;
}