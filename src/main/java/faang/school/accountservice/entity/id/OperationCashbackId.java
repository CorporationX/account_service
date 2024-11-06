package faang.school.accountservice.entity.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationCashbackId {
    private Long tariffId;
    private Long operationId;
}
