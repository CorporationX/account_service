package faang.school.accountservice.repository;

import faang.school.accountservice.entity.RateChangeRequest;
import faang.school.accountservice.enums.RateChangeRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RateChangeRequestRepository extends JpaRepository<RateChangeRequest, UUID> {
    Optional<RateChangeRequest> findByTariffIdAndEffectiveDate(UUID tariffId, LocalDate effectiveDate);

    List<RateChangeRequest> findByEffectiveDateAndStatus(LocalDate effectiveDate, RateChangeRequestStatus status);

    Optional<RateChangeRequest> findByTariffId(UUID tariffId);
}
