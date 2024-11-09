package faang.school.accountservice.entity;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "request_task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "request_id")
    private UUID requestId;

    @Column(name = "current_step")
    private Long currentHandlerStep;

    @Column(name = "request_task_status")
    private RequestStatus requestTaskStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "request", nullable = false)
    private Request request;
}
