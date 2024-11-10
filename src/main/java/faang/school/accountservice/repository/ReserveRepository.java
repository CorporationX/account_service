package faang.school.accountservice.repository;

import faang.school.accountservice.model.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    @Query(nativeQuery = true,value = """
        select * from reserve where request_id = ?1
        """)
    Optional<Reserve> findReserveByRequest(long requestId);

    @Query(nativeQuery = true, value = """
        select * from reserve where clear_scheduled_at <= ?1 and status = ?2
        """)
    List<Reserve> findOutOfDateReserves(LocalDateTime dateTime, String status);
}
