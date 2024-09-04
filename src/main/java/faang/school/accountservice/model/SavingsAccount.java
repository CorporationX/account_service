package faang.school.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor

@Entity
@Table(name = "savingsAccount")
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "tariff_history")
    private String tariffHistory;

    @Column(name = "last_calculated_date")
    private LocalDateTime lastCalculatedDate;

    @Version
    @Column(name = "version")
    private int version;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdateDate;
}
