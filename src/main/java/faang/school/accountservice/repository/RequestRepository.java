package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {


    boolean existsByLockValueAndUserId(Long lockValue, Long userId);

    boolean existsByInputDataAndUserId(Map<String, Object> inputData, Long userId);

    Request findByInputDataAndUserId(Map<String, Object> inputData, Long userId);

    Request findByLockValueAndUserId(Long lockValue);
}
