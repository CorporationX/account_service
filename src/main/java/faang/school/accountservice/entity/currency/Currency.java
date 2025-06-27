package faang.school.accountservice.entity.currency;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.base.BaseEntityWithAudit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString(exclude = {"accounts"})
@Entity
@Table(name = "currency")
public class Currency extends BaseEntityWithAudit {

    @Column(name = "name", length = 64, nullable = false, updatable = false)
    private String name;

    @Column(name = "iso_code", length = 8, nullable = false, updatable = false, unique = true)
    private String isoCode;

    @Column(name = "symbol", length = 4, updatable = false)
    private String symbol;

    @OneToMany(mappedBy = "currency")
    private List<Account> accounts = new ArrayList<>();
}
