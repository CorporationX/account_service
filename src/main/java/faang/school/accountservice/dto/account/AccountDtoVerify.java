package faang.school.accountservice.dto.account;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AccountDtoVerify {
    @Positive
    private long id;
    @AssertTrue
    private boolean isVerified;
    @NotNull
    private LocalDateTime updatedAt;
}
