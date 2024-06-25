package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceMapperTest {

    @InjectMocks
    private BalanceMapperImpl balanceMapper;

    @Test
    void shouldMapBalanceToDto() {
        Account account = mock(Account.class);
        when(account.getId()).thenReturn(1L);
        when(account.getNumber()).thenReturn("123456789012");
        when(account.getAccountType()).thenReturn(AccountType.CHECKING);
        when(account.getCurrency()).thenReturn(Currency.USD);
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);

        Balance balance = Balance.builder()
                .id(1L)
                .account(account)
                .currentBalance(BigDecimal.valueOf(1000))
                .authorizedBalance(BigDecimal.valueOf(2000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        BalanceDto balanceDto = balanceMapper.toDto(balance);

        assertThat(balanceDto.getId()).isEqualTo(1L);
        assertThat(balanceDto.getAccount().getId()).isEqualTo(1L);
        assertThat(balanceDto.getAccount().getNumber()).isEqualTo("123456789012");
        assertThat(balanceDto.getAccount().getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(balanceDto.getAccount().getCurrency()).isEqualTo(Currency.USD);
        assertThat(balanceDto.getAccount().getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(balanceDto.getCurrentBalance()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(balanceDto.getAuthorizedBalance()).isEqualTo(BigDecimal.valueOf(2000));
        assertThat(balanceDto.getCreatedAt()).isNotNull();
        assertThat(balanceDto.getUpdatedAt()).isNotNull();
    }
}