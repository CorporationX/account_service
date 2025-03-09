package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "tariff")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tariff_name", nullable = false)
    private String tariffName;

    @Column(name = "rate_history", columnDefinition = "TEXT")
    private String rateHistory;
}
