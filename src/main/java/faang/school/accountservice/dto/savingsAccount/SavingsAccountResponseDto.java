package faang.school.accountservice.dto.savingsAccount;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.dto.tariff.HistoryDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record SavingsAccountResponseDto(
    Long accountId,
    String tariffName,
    Double balance,
    List<HistoryDto> tariffHistory,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt) {
}
