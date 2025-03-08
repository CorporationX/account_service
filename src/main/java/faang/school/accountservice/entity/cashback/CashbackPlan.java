package faang.school.accountservice.entity.cashback;

import faang.school.accountservice.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cashback_plans")
public class CashbackPlan extends BaseEntity {
    @Column(name = "description", length = 256)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "cashback_plans_rules",
            joinColumns = @JoinColumn(name = "cashback_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "cashback_rule_id")
    )
    private List<CashbackRule> rules;
}
