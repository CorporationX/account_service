package faang.school.accountservice.service.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.client.AchievementServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.achievement.AchievementDto;
import faang.school.accountservice.entity.account.SavingAccount;
import faang.school.accountservice.repository.account.SavingAccountRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SavingAccountBonusService {
    private final SavingAccountRepository savingAccountRepository;
    private final AchievementServiceClient achievementServiceClient;
    private final UserContext userContext;

    private Map<String, Integer> achievementConfig;

    @SneakyThrows
    public SavingAccountBonusService(SavingAccountRepository savingAccountRepository,
                                     AchievementServiceClient achievementServiceClient,
                                     UserContext userContext,
                                     @Value("${task.saving-account-bonuses.achievements-config-file}")
                                     String achievementConfigPath,
                                     ObjectMapper objectMapper
    ) {
        this.savingAccountRepository = savingAccountRepository;
        this.achievementServiceClient = achievementServiceClient;
        this.userContext = userContext;
        this.achievementConfig = objectMapper.readValue(new ClassPathResource(achievementConfigPath).getFile(),
                objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Integer.class));
        log.info("Saving account. Achievements bonus config loaded: {}", achievementConfig);
    }

    @Async("savingAccountTaskExecutor")
    public void updateBonuses(List<SavingAccount> accounts) {
        userContext.setUserId(-1); // todo что проставлять для такого вызова?
        for (SavingAccount account : accounts) {
            updateBonuses(account);
            log.info("Bonuses updated for saving account '{}'", account.getAccount().getPaymentNumber());
        }
        savingAccountRepository.saveAll(accounts);
    }

    private void updateBonuses(SavingAccount account) {
        List<AchievementDto> achievements = achievementServiceClient.getByUserId(account.getAccount().getOwnerUserId());

        Integer bonuses = achievements.stream()
                .map(AchievementDto::getTitle)
                .map(t -> achievementConfig.getOrDefault(t, 0))
                .reduce(0, Integer::sum);

        account.setBonuses(bonuses);
        account.setBonusUpdatedAt(LocalDateTime.now());
        savingAccountRepository.save(account);
    }

}
