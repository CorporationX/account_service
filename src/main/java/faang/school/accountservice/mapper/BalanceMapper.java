package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.Balance;
import org.springframework.stereotype.Component;

@Component
public class BalanceMapper {

    public BalanceDto toDto(Balance balance) {
        if (balance == null) return null;

        return BalanceDto.builder()
                .accountId(balance.getAccount().getId())
                .authorized(balance.getAuthorizedBalance())
                .actual(balance.getActualBalance())
                .createdAt(balance.getCreatedAt())
                .updatedAt(balance.getUpdatedAt())
                .version(balance.getVersion())
                .build();
    }
}
