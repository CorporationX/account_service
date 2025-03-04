package faang.school.accountservice.repository;

import faang.school.accountservice.entity.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxEventsRepository extends JpaRepository<OutboxEvent, UUID> {

    @Modifying
    @Query("DELETE FROM OutboxEvent o WHERE o.id IN :ids")
    void deleteOutboxEventsById(@Param("ids") List<UUID> ids);

    @Query("SELECT o FROM OutboxEvent o ORDER BY o.createdAt ASC")
    List<OutboxEvent> findTopUnprocessed(Pageable pageable);
}
