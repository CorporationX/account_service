package faang.school.accountservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateDecreaseEvent {
    String title;
    List<Long> userIds;
}
