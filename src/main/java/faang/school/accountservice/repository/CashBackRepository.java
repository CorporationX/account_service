package faang.school.accountservice.repository;

import faang.school.accountservice.entity.CashBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashBackRepository  extends JpaRepository<CashBack, Long> {
}
