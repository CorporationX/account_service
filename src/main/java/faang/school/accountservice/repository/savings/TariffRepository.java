package faang.school.accountservice.repository.savings;

import faang.school.accountservice.model.savings.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

}
