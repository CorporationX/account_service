package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumber;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountNumberRepository extends JpaRepository<AccountNumber, UUID> {

        Optional<AccountNumber> findByAccountNumber(String accountNumber);

        Page<AccountNumber> findAllByAccountNumberStartsWithAndStatus (String prefix,
                                                                       AccountNumber.Status status,
                                                                       Pageable pageable);

        List<AccountNumber> findAllByType(String type);

        @Transactional
        @Modifying
        @Query(value = "update AccountNumber an " +
                "set an.status =:status " +
                "where an.accountNumber in :accountNumbers")
        int updateAccountNumbers(Collection<String> accountNumbers, AccountNumber.Status status);
}
