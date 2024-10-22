package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class BalanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
