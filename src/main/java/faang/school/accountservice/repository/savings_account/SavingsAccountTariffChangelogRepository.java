package faang.school.accountservice.repository.savings_account;

import faang.school.accountservice.entity.tariff.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccountTariffChangelogRepository extends JpaRepository<Tariff, Long> {
}
