package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccountNumbersSequence;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, Long> {

    /**
     * Создает новый счетчик для типа счета.
     */
    @Override
    <S extends AccountNumbersSequence> S save(S entity);

    /**
     * Инкрементирует значение счетчика для указанного типа счета.
     * Возвращает количество обновленных строк (1, если успешно; 0, если версия не совпала).
     */
    @Transactional
    @Modifying
    @Query("UPDATE AccountNumbersSequence ans " +
        "SET ans.currentValue = ans.currentValue + 1 " +
        "WHERE ans.accountType = :accountType " +
        "AND ans.version = :version")
    int incrementCounter(@Param("accountType") AccountType accountType, @Param("version") Long version);

    Optional<AccountNumbersSequence> findByAccountType(AccountType accountType);
}
