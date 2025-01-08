package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BalanceMapperTest {
    private static BalanceMapperImpl balanceMapper;

    private final long balanceId = 1L;
    private final long accountId = 2L;

    @BeforeAll
    public static void setUp() {
        balanceMapper = new BalanceMapperImpl();
    }

    @Test
    public void toEntitySuccessTest() {
        BalanceDto balanceDto = getBalanceDto();

        Balance balance = balanceMapper.toEntity(balanceDto);

        assertThat(balance).isNotNull();
        assertThat(balance.getId()).isEqualTo(balanceDto.getId());
        assertThat(balance.getVersion()).isEqualTo(balanceDto.getVersion());
        assertThat(balance.getActualBalance()).isEqualTo(balanceDto.getActualBalance());
        assertThat(balance.getAuthorizationBalance()).isEqualTo(balanceDto.getAuthorizationBalance());
        assertThat(balance.getCreatedAt()).isEqualTo(balanceDto.getCreatedAt());
        assertThat(balance.getUpdatedAt()).isEqualTo(balanceDto.getUpdatedAt());
        assertThat(balance.getAccount().getId()).isEqualTo(balanceDto.getAccountId());
    }

    @Test
    public void toEntityWithNullFailTest() {
        Balance balance = balanceMapper.toEntity(null);
        assertThat(balance).isNull();
    }

    @Test
    public void toDtoSuccessTest() {
        Balance balance = getBalance();

        BalanceDto balanceDto = balanceMapper.toDto(balance);

        assertThat(balance).isNotNull();
        assertThat(balanceDto.getId()).isEqualTo(balance.getId());
        assertThat(balanceDto.getVersion()).isEqualTo(balance.getVersion());
        assertThat(balanceDto.getActualBalance()).isEqualTo(balance.getActualBalance());
        assertThat(balanceDto.getAuthorizationBalance()).isEqualTo(balance.getAuthorizationBalance());
        assertThat(balanceDto.getCreatedAt()).isEqualTo(balance.getCreatedAt());
        assertThat(balanceDto.getUpdatedAt()).isEqualTo(balance.getUpdatedAt());
        assertThat(balanceDto.getAccountId()).isEqualTo(balance.getAccount().getId());
    }

    @Test
    public void toDtoWithNullFailTest() {
        BalanceDto balanceDto = balanceMapper.toDto(null);
        assertThat(balanceDto).isNull();
    }

    private Balance getBalance() {
        LocalDateTime now = LocalDateTime.now();

        return Balance.builder()
                .id(balanceId)
                .actualBalance(BigDecimal.valueOf(700))
                .authorizationBalance(BigDecimal.valueOf(150))
                .createdAt(now)
                .updatedAt(now)
                .version(1)
                .account(getAccount())
                .build();
    }

    private Account getAccount() {
        return Account.builder()
                .id(accountId)
                .build();
    }

    private BalanceDto getBalanceDto() {
        LocalDateTime now = LocalDateTime.now();

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(balanceId);
        balanceDto.setActualBalance(BigDecimal.valueOf(800));
        balanceDto.setActualBalance(BigDecimal.valueOf(200));
        balanceDto.setCreatedAt(now);
        balanceDto.setUpdatedAt(now);
        balanceDto.setVersion(1);
        balanceDto.setAccountId(accountId);

        return balanceDto;
    }
}
