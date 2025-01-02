package faang.school.accountservice.repository;

import faang.school.accountservice.model.Request;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByIdempotencyToken(UUID idempotencyToken);

    @Query(nativeQuery = true, value = """
            WITH base_requests AS (
                SELECT r.*
                FROM request AS r
                WHERE r.is_open = true
                    AND r.request_status = 'TODO'
                    AND r.request_type = :requestType
                    AND NOT EXISTS (SELECT 1
                                    FROM request r2
                                    WHERE r.payment_account_id = r2.payment_account_id
                                    AND r2.is_open = TRUE
                                    AND r2.request_status = 'IN_PROGRESS')
            ),
            ranked_requests AS (
                SELECT *, row_number() OVER (PARTITION BY payment_account_id ORDER BY created_at) AS rn
                FROM base_requests
            ),
            requests_to_update AS (
                SELECT id
                FROM ranked_requests
                WHERE rn = 1
                ORDER BY created_at
                LIMIT :limit
                FOR UPDATE SKIP LOCKED
            ),
            update_requests AS (
                UPDATE request r
                SET request_status = 'IN_PROGRESS'
                WHERE r.id IN (
                        SELECT id
                        FROM requests_to_update)
                RETURNING *
            )
            SELECT * FROM update_requests
            ORDER BY created_at;
            """)
    @Modifying
    List<Request> getRequestToExecuteByType(@Param("requestType") String requestType,
                                            @Param("limit") int limit);

    @Query(nativeQuery = true, value = """
            UPDATE request
            SET request_status = :requestStatus
            WHERE id = :requestId;
            """)
    @Modifying
    void updateRequestStatusById(@Param("requestId") Long requestId,
                                 @Param("requestStatus") String requestStatus);

    @Query(nativeQuery = true, value = """
            UPDATE request
            SET request_status = 'COMPLETED',
                is_open = FALSE
            WHERE id = :requestId
            """)
    @Modifying
    void completeRequest(@Param("requestId") Long requestId);
}
