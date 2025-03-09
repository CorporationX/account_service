package faang.school.accountservice.model.tariff;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "tariff")
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TariffType type;

    @Type(JsonType.class)
    @Column(name = "rate_history", nullable = false, columnDefinition = "jsonb")
    private List<BigDecimal> rateHistory;

    public BigDecimal getActualRate() {
        return rateHistory.get(rateHistory.size() - 1);
    }
}