package faang.school.accountservice.repository;

import faang.school.accountservice.entity.MerchantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantMappingRepository extends JpaRepository<MerchantMapping, Long> {
}
