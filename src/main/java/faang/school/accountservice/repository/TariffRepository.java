package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    @Query("SELECT t FROM Tariff t " +
            "JOIN t.savingsAccount s " +
            "JOIN s.account a " +
            "WHERE a.number = :number")
    Optional<Tariff> findTariffByAccountNumber(@Param("number")String accountNumber);

    @Query("SELECT t FROM Tariff t " +
            "JOIN t.savingsAccount s " +
            "JOIN s.account a " +
            "JOIN FETCH t.rateHistoryList r " +
            "WHERE a.number = :number")
    Optional<Tariff> findTariffWithRateHistoryByAccountNumber(@Param("number") String accountNumber);
}
