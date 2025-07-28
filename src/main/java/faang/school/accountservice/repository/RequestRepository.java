package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM requests WHERE idempotent_token = ?1 FOR UPDATE
            """)
    Optional<Request> findByIdempotentTokenForUpdate(String idempotentToken);


    @Query(nativeQuery = true, value = """
            SELECT * FROM requests WHERE id = ?1 FOR UPDATE
            """)
    Optional<Request> findByIdForUpdate(Long id);

    List<Request> findAllByStatus(RequestStatus status);


    @Query(nativeQuery = true, value = """
            SELECT * FROM requests WHERE value_lock = ?1 FOR UPDATE
            """)
    Optional<Request> findByValueLock(String value);
}
