package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

	@Value("${account.number.batch.size}")
	private int batchSize;

	private final FreeAccountNumbersService freeAccountNumberService;

	@Scheduled(cron = "${account.number.schedule.personal-checking}")
	public void generatePersonalChecking() {
		freeAccountNumberService.generateAccountNumber(AccountType.PERSONAL_CHECKING, batchSize);
	}

	@Scheduled(cron = "${account.number.schedule.business-checking}")
	public void generateBusinessChecking() {
		freeAccountNumberService.generateAccountNumber(AccountType.BUSINESS_CHECKING, batchSize);
	}

	@Scheduled(cron = "${account.number.schedule.savings}")
	public void generateSavings() {
		freeAccountNumberService.generateAccountNumber(AccountType.SAVINGS, batchSize);
	}

	@Scheduled(cron = "${account.number.schedule.currency}")
	public void generateCurrency() {
		freeAccountNumberService.generateAccountNumber(AccountType.CURRENCY, batchSize);
	}

	@Scheduled(cron = "${account.number.schedule.deposit}")
	public void generateDeposit() {
		freeAccountNumberService.generateAccountNumber(AccountType.DEPOSIT, batchSize);
	}
}
