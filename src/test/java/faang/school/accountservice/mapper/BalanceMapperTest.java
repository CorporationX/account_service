package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BalanceMapperTest {

    private TransactionDto transactionDto;
    private Balance balance;

    @InjectMocks
    private BalanceMapperImpl balanceMapper;

    @Test
    @DisplayName("success mapping BalanceDto to Balance")
    void testToBalanceDto() {

        balance = Balance.builder()
                .id(1)
                .authorizationBalance(BigDecimal.valueOf(100.00))
                .actualBalance(BigDecimal.valueOf(150.00))
                .paymentNumber(12345L)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2023, 10, 5, 12, 0))
                .build();

        BalanceDto balanceDto = balanceMapper.toBalanceDto(balance);

        assertEquals(balanceDto.getId(), balance.getId());
        assertEquals(balanceDto.getAuthorizationBalance(), balance.getAuthorizationBalance());
        assertEquals(balanceDto.getActualBalance(), balance.getActualBalance());
        assertEquals(balanceDto.getCreatedAt(), balance.getCreatedAt());
        assertEquals(balanceDto.getUpdatedAt(), balance.getUpdatedAt());
        assertEquals(balanceDto.getVersion(), balance.getVersion());
    }
}
