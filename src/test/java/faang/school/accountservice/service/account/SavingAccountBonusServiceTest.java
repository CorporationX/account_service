package faang.school.accountservice.service.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.client.AchievementServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.achievement.AchievementDto;
import faang.school.accountservice.dto.achievement.Rarity;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.enums.currency.Currency;
import faang.school.accountservice.repository.account.SavingAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SavingAccountBonusServiceTest {
    @Mock
    private SavingAccountRepository savingAccountRepository;
    @Mock
    private AchievementServiceClient achievementServiceClient;
    @Mock
    private UserContext userContext;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private String achievementConfigPath = "config/test-saving-account-achievements-bonus-config.json";
    private SavingAccountBonusService savingAccountBonusService;

    @BeforeEach
    public void init() {
        savingAccountBonusService = new SavingAccountBonusService(
                savingAccountRepository,
                achievementServiceClient,
                userContext,
                achievementConfigPath,
                objectMapper
        );
    }

    @Test
    void testUpdateBonuses() {
        List<AchievementDto> achievements = List.of(createAchievement());
        Mockito.when(achievementServiceClient.getByUserId(any())).thenReturn(achievements);

        List<SavingAccount> accounts = List.of(
                createSavingAccount(1, "111111111111"),
                createSavingAccount(2, "222222222222"),
                createSavingAccount(3, "333333333333")
        );
        assertTrue(accounts.stream().allMatch(a -> a.getBonuses() == 0));

        savingAccountBonusService.updateBonuses(accounts);

        assertAll(
                () -> assertTrue(accounts.stream().noneMatch(a -> a.getBonuses() == 0)),
                () -> assertTrue(accounts.stream().allMatch(a -> a.getBonuses() == 10))
        );
    }

    private AchievementDto createAchievement() {
        return AchievementDto.builder()
                .title("SENSEI")
                .rarity(Rarity.EPIC)
                .points(100)
                .build();
    }

    private SavingAccount createSavingAccount(long index, String number) {
        return SavingAccount.builder()
                .id(index)
                .account(Account.builder()
                        .id(index)
                        .paymentNumber(number)
                        .ownerUserId(index)
                        .balance(BigDecimal.ZERO)
                        .type(AccountType.SAVING_ACCOUNT)
                        .currency(Currency.EUR)
                        .status(AccountStatus.ACTIVE)
                        .build())
                .tariff(Tariff.builder()
                        .id(index)
                        .name("tariff_" + index)
                        .rate(BigDecimal.valueOf(16))
                        .build())
                .bonuses(0)
                .build();
    }

}
