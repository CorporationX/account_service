package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountStatementRepository extends JpaRepository<AccountStatement, Long> {

    boolean existsByKey(String key);

    AccountStatement findByKey(String key);

    int countAllByPost_Id(Long postId);

    void deleteByKey(String key);
}