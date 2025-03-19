package faang.school.accountservice.repository;

import faang.school.accountservice.entity.TransferRequest;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<TransferRequest, UUID> {

    default boolean existsByIdOrThrow(UUID id) {
        try {
            return existsById(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Not found transfer request with id " + id);
        }
    }

    default TransferRequest findByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Not found transfer request with id " + id));
    }

    @Query("SELECT t FROM TransferRequest t WHERE t.kafkaPublished = false ORDER BY t.updatedAt ASC")
    List<TransferRequest> findTopUnprocessed(Pageable pageable);
}
