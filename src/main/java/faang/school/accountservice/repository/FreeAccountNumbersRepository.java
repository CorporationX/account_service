package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
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
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM free_account_numbers " +
        "WHERE account_type = :accountType " +
        "RETURNING account_number", nativeQuery = true)
    Optional<String> getAndDeleteFirstFreeAccountNumber(String accountType);

    /**
     * Добавляет новый свободный номер счета.
     */
    @Override
    <S extends FreeAccountNumber> S save(S entity);

    /**
     * Найти первый свободный номер счета для конкретного типа счета.
     *
     * @param accountType тип счета
     * @return Optional с первым свободным номером счета
     */
    Optional<FreeAccountNumber> findFirstByAccountType(AccountType accountType);
}
