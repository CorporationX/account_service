package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {

    @Query("SELECT r FROM Request  r WHERE r.status = :status AND r.scheduledAt <= :currentTime")
    List<Request> findPendingRequests(@Param("status")RequestStatus status, @Param("currentTime") LocalDateTime currentTime);
}
