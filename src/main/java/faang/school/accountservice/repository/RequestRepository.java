package faang.school.accountservice.repository;

import faang.school.accountservice.model.Request;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {

    @Override
    Request save(Request request) throws DataIntegrityViolationException;

    @Query(nativeQuery = true, value = "SELECT user_id, COUNT(*) as result_count FROM request" +
            "WHERE (created_at > :time OR updated_at > :time) GROUP BY user_id HAVING COUNT(*) > :maxNumberOfRequests")
    Map<Long, Long> findAllGroupedByUserIdForPeriod(ZonedDateTime time, Long maxNumberOfRequests);

    @Query(nativeQuery = true, value = "SELECT * FROM request WHERE id = ?1 FOR UPDATE")
    Optional<Request> findByIdForUpdate(Long id);

    @Query(nativeQuery = true, value =
            "SELECT * FROM request WHERE request_status = 0 ORDER BY created_at ASC LIMIT ?1")
    List<Request> findSomeRequestsForExecute(long limit);

    List<Request> findAllByUserId(Long userId);
}