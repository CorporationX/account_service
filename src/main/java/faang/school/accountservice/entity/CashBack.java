package faang.school.accountservice.entity;

import faang.school.accountservice.enums.CashBackType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cash_back", nullable = false)
    private CashBackType type;
}
