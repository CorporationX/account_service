package faang.school.accountservice.entity;

import faang.school.accountservice.enums.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "number", length = 20, nullable = false, unique = true)
    private String number;

    @Column(name = "owner", nullable = false)
    private Owner owner;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "currency")
    private Currency currency;

    @Column(name = "status", nullable = false)
    private Status status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "version")
    private String version;

    public enum Status {
        ACTIVE, CLOSED, FROZEN
    }

    public enum Type {
        CURRENT, SETTLEMENT, CREDIT, DEPOSIT, BUDGET
    }

    public enum Owner {
        PROJECT, USER
    }

    @PrePersist
    public void assignId() {
        if (number == null) {
            this.number = generateNumber();
        }
    }
    //случайная генерация id
    private String generateNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder randomPart = new StringBuilder();
        Long timePart = System.currentTimeMillis();
        randomPart.append(timePart);
        int randomPartLength = random.nextInt(12, 21) - String.valueOf(timePart).length();

        for (int i = 1; i < randomPartLength; i++) {
            randomPart.append(random.nextInt(10));
        }
        return randomPart.toString();
    }
}
