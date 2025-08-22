package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByNumber(String number);

    List<Account> findByUserId(Long userId);

    List<Account> findByProjectId(Long projectId);
}
