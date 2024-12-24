package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {

    /**
     * Транзакционно находит первый свободный номер счета, удаляет его из таблицы и возвращает.
     * Используется DELETE ... RETURNING.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM FreeAccountNumber f WHERE f.accountType = :accountType")
    int getAndDeleteFirstFreeAccountNumber(@Param("accountType") AccountType accountType);

    Optional<FreeAccountNumber> findFirstByAccountType(AccountType accountType);

    /**
     * Добавляет новый свободный номер счета.
     */
    @Override
    <S extends FreeAccountNumber> S save(S entity);
}
