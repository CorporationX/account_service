package faang.school.accountservice.generator;

import faang.school.accountservice.config.account.AccountProperties;
import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.service.account.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FreeAccountNumbersGeneratorScheduler {

    private final AccountProperties accountProperties;
    private final FreeAccountNumbersService freeAccountNumbersService;

    /**
     * JavaDoc по просьбам трудящихся
     * Данный метод каждые N (в данном враианте каждые 10 минут) времени проверяет наличие
     * определенного в .yaml файле количества свободных номеров для каждого из типов аккаунтов DEBIT, CREDIT etc.
     * Если необходимого количества нет (меньше указанного),
     * например нужно чтобы было в запасе 400 номеров с типом DEBIT,
     * количество определяется путем изначального подсчета фактического количество номеров в таблице FreeAccountNumbers,
     * определенного типа, далее вычитаем из нужного количества фактическое -
     * получаем количество которое нужно сгенерировать, обращаемся к FreeAccountService генерирует пачку номеров
     * и так делается для каждого из представленных в .yaml файле типов аккаунтов
     */
    @Transactional
    @Scheduled(cron = "${account.accountNumbers.scheduler.cron}")
    public void createRequiredFreeAccountNumbersPool() {
        Map<AccountEnum, Integer> needsOfFreeAccountNumbers =
                accountProperties.getAccountNumbers().getNeedsOfFreeAccountNumbers();
        for (Map.Entry<AccountEnum, Integer> entry : needsOfFreeAccountNumbers.entrySet()) {
            AccountEnum accountType = entry.getKey();
            int freeAccountNeeds = entry.getValue();
            int existingAccountsNumberByType = freeAccountNumbersService
                    .countAllFreeAccountNumbersByType(accountType);
            if ((freeAccountNeeds - existingAccountsNumberByType) > 0) {
                freeAccountNumbersService.generateAccountNumbers(accountType,
                        freeAccountNeeds - existingAccountsNumberByType);
            }
        }
    }
}
