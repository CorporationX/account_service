package faang.school.accountservice.entity;

import faang.school.accountservice.converter.RateHistoryConverter;
import faang.school.accountservice.enums.TariffType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tariff")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private TariffType name;

    @Column(name = "rate_history", nullable = false, columnDefinition = "jsonb")
    @Convert(converter = RateHistoryConverter.class)
    private List<BigDecimal> rateHistory;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
