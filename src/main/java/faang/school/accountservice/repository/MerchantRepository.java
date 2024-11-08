package faang.school.accountservice.repository;

import faang.school.accountservice.entity.type.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
