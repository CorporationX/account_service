package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BalanceMapperTest {

    private BalanceMapper balanceMapper;

    @BeforeEach
    void setUp() {
        balanceMapper = Mappers.getMapper(BalanceMapper.class);
    }


    @Test
    void toDto() {
        Account account = new Account();
        account.setId(1L);

        Balance balance = Balance.builder()
                .id(10L)
                .account(account)
                .authorizedBalance(new BigDecimal("50.00"))
                .actualBalance(new BigDecimal("100.00"))
                .build();

        BalanceDto dto = balanceMapper.toDto(balance);

        assertEquals(account.getId(), dto.getAccountId());
        assertEquals(balance.getAuthorizedBalance(), dto.getAuthorizedBalance());
        assertEquals(balance.getActualBalance(), dto.getActualBalance());
    }

    @Test
    void toEntity() {
        BalanceDto dto = BalanceDto.builder()
                .accountId(1L)
                .authorizedBalance(new BigDecimal("75.00"))
                .actualBalance(new BigDecimal("150.00"))
                .build();

        Account account = new Account();
        account.setId(1L);

        Balance balance = balanceMapper.toEntity(dto, account);

        assertEquals(dto.getAuthorizedBalance(), balance.getAuthorizedBalance());
        assertEquals(dto.getActualBalance(), balance.getActualBalance());
        assertEquals(account, balance.getAccount());

        assertNull(balance.getId());
        assertNull(balance.getCreatedAt());
        assertNull(balance.getUpdatedAt());
        assertNull(balance.getVersion());
    }
}