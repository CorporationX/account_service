package faang.school.accountservice.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "context")
    private String context;

    @Timestamp
    @Column(name = "scheduled_at")
    private String scheduledAt;

    @JoinColumn(name = "balance", unique = true)
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
}
