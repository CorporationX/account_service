package faang.school.accountservice.dto.savings;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SavingsAccountDto {
    private Long id;
    private long accountId;

    private List<Long> tariffHistory;

    private LocalDate lastInterestCalculationDate;

    private long version;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
