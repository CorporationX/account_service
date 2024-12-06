
package faang.school.accountservice.entity.request;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.owner.Owner;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "request")
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "UUID")
    private UUID id;

    private String context;

    @Column(name = "request_status")
    private RequestStatus requestStatus;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @OneToMany( mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestTask> requestTask;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "balance_id", nullable = false)
    private Balance balance;

    @PrePersist
    public void prePersist() {
        if(id == null) {
            id = UUID.randomUUID();
        }
    }
}

