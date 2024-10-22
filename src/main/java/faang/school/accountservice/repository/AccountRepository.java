package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByNumber(String number);

    List<Account> findAllByUserId(Long id);
}
