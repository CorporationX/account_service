package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.Account;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    default Account findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
    }
}