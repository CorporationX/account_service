package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_number")
public class AccountNumber {

    @Id
    private String accountNumber;

    @Column
    private String type;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        NOT_AVAILABLE, AVAILABLE
    }
}
