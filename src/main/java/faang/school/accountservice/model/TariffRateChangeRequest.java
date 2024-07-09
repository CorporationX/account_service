package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tariff_rate_change_request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffRateChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tariff_id", nullable = false)
    @JoinColumn(name = "tariff_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_tariff_rate_change_request_tariff"))
    private Long tariffId;

    @Column(name = "new_rate")
    private BigDecimal newRate;

    @Column(name = "requested_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime requestedAt;

    @Column(name = "change_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime changeDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public enum RequestStatus{
        PENDING, NOTIFIED, COMPLETED
    }
}
