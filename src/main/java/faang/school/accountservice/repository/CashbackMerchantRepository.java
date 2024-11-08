package faang.school.accountservice.repository;

import faang.school.accountservice.entity.cacheback.CashbackMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CashbackMerchantRepository extends JpaRepository<CashbackMerchant, UUID> {
}