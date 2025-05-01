package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.exception.DuplicateAccountNumberException;
import faang.school.accountservice.exception.SequenceNotInitializedException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import static faang.school.accountservice.constants.Constants.DUBLICATE_ACCOUNT_NUMBERS_E;
import static faang.school.accountservice.constants.Constants.SEQ_NOT_INIT_MSG;

@Service
@Slf4j
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService {
    private final FreeAccountNumbersRepository numbersRepository;
    private final AccountNumbersSequenceRepository sequenceRepository;

    @Override
    public void addFreeAccountNumber(String accountType, String accountNumber) {
        log.info("Adding free account number {} for type {}", accountNumber, accountType);
        FreeAccountNumber.Key key = new FreeAccountNumber.Key(accountType, accountNumber);
        FreeAccountNumber entity = new FreeAccountNumber(key, Instant.now());

        if (numbersRepository.existsById(key)) {
            log.warn("addFreeAccountNumber: duplicate detected (type={}, number={})", accountType, accountNumber);
            throw new DuplicateAccountNumberException(DUBLICATE_ACCOUNT_NUMBERS_E + accountNumber);
        }
        numbersRepository.save(entity);
        log.info("addFreeAccountNumber: saved entity {}", entity);
    }

    @Override
    @Transactional
    public <R> R withNewAccountNumber(String accountType, Function<String, R> action, String prefix, int totalLength) {
        log.info("withNewAccountNumber: trying free number for type={}", accountType);

        Optional<FreeAccountNumber> freeOpt = numbersRepository
                .findFirstByKeyAccountTypeOrderByCreatedAtAsc(accountType);

        String number;
        if (freeOpt.isPresent()) {
            FreeAccountNumber free = freeOpt.get();
            number = free.getKey().getAccountNumber();
            log.info("withNewAccountNumber: found free number {} (createdAt={})",
                    number, free.getCreatedAt());
            numbersRepository.delete(free);
            log.debug("withNewAccountNumber: deleted entity {}", free);
        } else {
            log.info("withNewAccountNumber: no free numbers for type={}, will generate new", accountType);
            AccountNumbersSequence seq = sequenceRepository.findById(accountType)
                    .orElseThrow(() -> {
                        log.error("withNewAccountNumber: sequence not initialized for type={}", accountType);
                        return new SequenceNotInitializedException(SEQ_NOT_INIT_MSG + accountType);
                    });

            long next = seq.getCurrentValue() + 1;
            seq.setCurrentValue(next);
            sequenceRepository.save(seq);
            log.info("withNewAccountNumber: sequence incremented to {}", next);

            String numberPart = String.format("%0" + (totalLength - prefix.length()) + "d", next);
            number = prefix + numberPart;
            log.info("withNewAccountNumber: generated new number {}", number);
        }

        R result = action.apply(number);
        log.debug("withNewAccountNumber: action applied, returning result");
        return result;
    }
}

