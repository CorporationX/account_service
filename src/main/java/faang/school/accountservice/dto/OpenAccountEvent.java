package faang.school.accountservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OpenAccountEvent {
    private Long Id;
    private String number;
}
