package faang.school.accountservice.entity.cashback.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashbackId {
    private Long tariffId;
    private Long typeId;
}
