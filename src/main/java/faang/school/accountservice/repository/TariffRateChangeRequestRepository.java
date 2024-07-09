package faang.school.accountservice.repository;

import faang.school.accountservice.model.TariffRateChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TariffRateChangeRequestRepository extends JpaRepository<TariffRateChangeRequest, Long> {
    Optional<TariffRateChangeRequest> findByTariffIdAndChangeDateAndStatus(Long tariffId,
                                                                           LocalDateTime changeDate,
                                                                           TariffRateChangeRequest.RequestStatus status);
}
