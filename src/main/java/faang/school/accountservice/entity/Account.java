package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import org.springframework.data.relational.core.mapping.Table;


@Entity
@Table(name = "account")
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Balance balance;

}
