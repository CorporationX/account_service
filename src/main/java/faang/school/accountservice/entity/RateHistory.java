package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rate_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
}
