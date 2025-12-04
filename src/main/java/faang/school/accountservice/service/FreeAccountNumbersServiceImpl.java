package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService {

	@Value("${account.number.prefix.personal-checking}")
	private long personalCheckingPrefix;
	@Value("${account.number.prefix.business-checking}")
	private long businessCheckingPrefix;
	@Value("${account.number.prefix.savings}")
	private long savingsPrefix;
	@Value("${account.number.prefix.currency}")
	private long currencyPrefix;
	@Value("${account.number.prefix.deposit}")
	private long depositPrefix;

	private final AccountNumbersSequenceRepository accountSequenceRepository;
	private final FreeAccountNumbersRepository freeAccountRepository;

	@Override
	@Transactional
	public void generateAccountNumber(AccountType type, int batchSize) {
		List<Long> result = accountSequenceRepository.incrementCounter(type.name(), batchSize);
		if (result.isEmpty()) {
			throw new EntityNotFoundException("Failed to increment counter for type: " + type);
		}
		long newCounter = result.get(0);
		long baseCounter = newCounter - batchSize + 1;

		List<FreeAccountNumber> numbers = new ArrayList<>();
		for (long i = 0; i < batchSize; i++) {
			long accountNumber = getAccountNumber(type, baseCounter + i);
			numbers.add(new FreeAccountNumber(new FreeAccountId(type, accountNumber)));
		}
		freeAccountRepository.saveAll(numbers);
	}

	@Override
	@Transactional
	public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
		Long accountNumber;
		do {
			accountNumber =  freeAccountRepository.pollFirst(type.name());
			if (accountNumber == null) {
				generateAccountNumber(type, 1);
			}
		} while (accountNumber == null);

		numberConsumer.accept(new FreeAccountNumber(new FreeAccountId(type, accountNumber)));
	}

	private long getAccountNumber(AccountType type, long sequenceValue) {
		return switch (type) {
			case PERSONAL_CHECKING -> personalCheckingPrefix + sequenceValue;
			case BUSINESS_CHECKING -> businessCheckingPrefix + sequenceValue;
			case SAVINGS -> savingsPrefix + sequenceValue;
			case CURRENCY -> currencyPrefix + sequenceValue;
			case DEPOSIT -> depositPrefix + sequenceValue;
			default -> throw new ForbiddenException("Unsupported account type: " + type);
		};
	}
}
