package faang.school.accountservice.repository.currency;

import faang.school.accountservice.entity.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, UUID> {
}
