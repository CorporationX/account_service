package faang.school.accountservice.repository.request;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/*@Repository
public interface RequestRepository extends JpaRepository<Request, String> {

    @LazyCollection(LazyCollectionOption.TRUE)
    @Query("SELECT p FROM Request p WHERE p.scheduledAt = CURRENT_TIMESTAMP")
    List<Request> findScheduledAt();
}*/
