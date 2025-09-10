package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountNumberRepository extends JpaRepository<AccountNumber, UUID> {

        Optional<AccountNumber> findByAccountNumber(String accountNumber);

        Page<AccountNumber> findAllByAccountNumberStartsWithAndStatus (String prefix,
                                                                       AccountNumber.Status status,
                                                                       Pageable pageable);
}
