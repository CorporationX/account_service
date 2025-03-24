package faang.school.accountservice.repository;

import faang.school.accountservice.entity.cashback.CashbackRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashbackRuleRepository extends JpaRepository<CashbackRule, Long> {

    long countByIdIn(List<Long> ids);
}
