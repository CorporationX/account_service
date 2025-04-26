package faang.school.accountservice.repository;

import faang.school.accountservice.entity.RequestEvent;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;
import java.util.List;

@Repository
public interface RequestEventRepository extends JpaRepository<RequestEvent, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM RequestEvent r WHERE r.id IN :ids")
    void deleteAllByIds(Collection<UUID> ids);

    List<RequestEvent> findByOrderByCreatedAtDesc(Pageable pageable);
}
