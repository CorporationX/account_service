package faang.school.accountservice.service.account;

import faang.school.accountservice.config.account.AccountProperties;
import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.account.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final AccountProperties properties;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final JdbcTemplate template;

    @Transactional
    public void generateAccountNumbers(AccountEnum type, int batchSize) {
        AccountNumbersSequence ans = accountNumbersSequenceRepository.findCounterByType(type.name())
                .orElseGet(() -> generateFirstAccountNumberSequenceForType(type));

        List<FreeAccountNumber> numberList = new ArrayList<>();
        for (long i = ans.getCounter(); i <= batchSize; i++) {
            FreeAccountId freeAccountId = new FreeAccountId(type, type.getPrefix() +
                    String.format(properties.getAccountNumbers().getNumberFormatter(), i));
            FreeAccountNumber freeAccountNumber = new FreeAccountNumber(freeAccountId);
            numberList.add(freeAccountNumber);
        }
        freeAccountNumbersBatchSave(numberList);
        accountNumbersSequenceRepository.updateCounterForType(type.name(), batchSize);
    }

    @Transactional
    public void retrieveAccountNumber(AccountEnum type, Consumer<FreeAccountNumber> numberConsumer) {
        FreeAccountNumber freeAccountNumber = freeAccountNumbersRepository.findFirstFreeAccountNumberByType(type.name())
                .orElseGet(() -> generateFreeAccountNumber(type));

        freeAccountNumbersRepository.deleteFreeAccountNumberByTypeAndAccountNumber(
                type.name(),
                freeAccountNumber.getId().getAccountNumber()
        );

        numberConsumer.accept(freeAccountNumber);
    }

    @Transactional
    public FreeAccountNumber generateFreeAccountNumber(AccountEnum type) {
        AccountNumbersSequence ans = accountNumbersSequenceRepository.findCounterByType(type.name())
                .orElseGet(() -> generateFirstAccountNumberSequenceForType(type));

        long initialValue = ans.getCounter();
        FreeAccountId freeAccountId = new FreeAccountId(type, type.getPrefix() +
                String.format(properties.getAccountNumbers().getNumberFormatter(), initialValue));
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(freeAccountId);
        freeAccountNumbersRepository.save(freeAccountNumber);
        return freeAccountNumber;
    }

    @Transactional
    private AccountNumbersSequence generateFirstAccountNumberSequenceForType(AccountEnum type) {
        AccountNumbersSequence ans = new AccountNumbersSequence();
        ans.setType(type.name());
        ans.setCounter(1);
        accountNumbersSequenceRepository.save(ans);
        return ans;
    }

    public int countAllFreeAccountNumbersByType(AccountEnum accountType) {
        return freeAccountNumbersRepository.countAllFreeAccountNumbersByType(accountType);
    }

    public void freeAccountNumbersBatchSave(List<FreeAccountNumber> freeAccountNumbers) {
        String sql = properties.getAccountNumbers().getBatchSaveSQL();
        log.debug("Starting batch save counting {} entities.", freeAccountNumbers.size());
        try {
            template.batchUpdate(sql, freeAccountNumbers, freeAccountNumbers.size(),
                    (PreparedStatement ps, FreeAccountNumber accountNumber) -> {
                        ps.setString(1, String.valueOf(accountNumber.getId().getType()));
                        ps.setString(2, accountNumber.getId().getAccountNumber());
                    });
        } catch (DataAccessException dae) {
            log.error("Error occurred while batch save! {}", dae.getMessage());
            throw new RuntimeException();
        }
    }
}
