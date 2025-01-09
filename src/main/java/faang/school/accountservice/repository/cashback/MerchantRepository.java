package faang.school.accountservice.repository.cashback;

import faang.school.accountservice.model.cashback.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}