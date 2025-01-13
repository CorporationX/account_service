package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "free_account_numbers")
@IdClass(FreeAccountId.class)
@Data
public class FreeAccount {

    @Id
    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Id
    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;
}
