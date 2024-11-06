package faang.school.accountservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OpenAccountEvent {
    private final Long Id;
    private final String number;
}
