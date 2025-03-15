package faang.school.accountservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateChangeNotificationEvent {
    private Long tariffId;
    private String oldRate;
    private String newRate;
    private String date;
}