package faang.school.accountservice.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "idempotency_token")
public class IdempotencyToken {
    @Id
    private UUID token;
    @OneToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;
}
