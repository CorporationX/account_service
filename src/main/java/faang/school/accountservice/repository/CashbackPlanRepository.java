package faang.school.accountservice.repository;

import faang.school.accountservice.entity.cashback.CashbackPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CashbackPlanRepository extends JpaRepository<CashbackPlan, Long> {

    @Query(value =
            """
                    INSERT INTO cashback_plans_rules (cashback_plan_id, cashback_rule_id)
                    SELECT :planId, id
                    FROM cashback_rules
                    WHERE id IN :rulesIds
                    """,
            nativeQuery = true
    )
    @Modifying
    int addRulesToPlan(Long planId, List<Long> rulesIds);
}
