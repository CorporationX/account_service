package faang.school.accountservice.repository;

import faang.school.accountservice.model.CashbackMerchantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashbackMerchantMappingRepository extends JpaRepository<CashbackMerchantMapping, Long> {
    Optional<CashbackMerchantMapping> findByTariffIdAndMerchant(Long tariffId, String merchant);

    List<CashbackMerchantMapping> findByTariffId(Long tariffId);

    void deleteByTariffIdAndMerchant(Long tariffId, String merchant);
}
