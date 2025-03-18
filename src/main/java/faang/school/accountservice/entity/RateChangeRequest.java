package faang.school.accountservice.entity;

import faang.school.accountservice.enums.RateChangeRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_change_request")
public class RateChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "new_rate", nullable = false)
    private Double newRate;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RateChangeRequestStatus status;

    @Column(name = "processed", nullable = false)
    private boolean processed;

}
