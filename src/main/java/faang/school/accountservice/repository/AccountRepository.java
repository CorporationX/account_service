package faang.school.accountservice.repository;

import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("""
    SELECT a FROM Account a
    JOIN a.owner o
    WHERE o.personId = :personId
    AND o.ownerType = :ownerType
   """)
    List<Account> findByPersonIdAndOwnerType(@Param("personId") Long personId,
                                               @Param("ownerType") OwnerType ownerType);
}