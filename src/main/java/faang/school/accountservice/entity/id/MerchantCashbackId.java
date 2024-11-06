package faang.school.accountservice.entity.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantCashbackId {
    private Long tariffId;
    private Long merchantId;
}
