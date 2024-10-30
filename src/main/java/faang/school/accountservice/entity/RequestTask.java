package faang.school.accountservice.entity;

import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "handler")
    @Enumerated(EnumType.STRING)
    private RequestHandlerType handler;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    @Version
    private int version;

    @JoinColumn(name = "account_id")
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;
}
