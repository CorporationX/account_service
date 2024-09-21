package faang.school.accountservice.repository;

import faang.school.accountservice.entity.CashbackMerchantMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashbackMerchantMappingRepository extends JpaRepository<CashbackMerchantMapping, Long> {
    List<CashbackMerchantMapping> findByCashbackTariffId(Long tariffId);
}