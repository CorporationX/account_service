package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumbers;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.InvalidAccountNumberException;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FreeAccountNumberPoolService {

    private final FreeAccountNumbersRepository repository;
    private final AccountNumberValidator validator;

    public FreeAccountNumbers addToPool(AccountType type, String number) {
        validator.validate(type, number);

        try {
            return repository.createFreeAccountNumber(type, number);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidAccountNumberException(
                    String.format("Number '%s' already exists", number), e);
        }
    }

    public Optional<String> retrieveFromPool(AccountType type) {
        return repository.findAndDeleteFirstAvailableNumber(type.name())
                .filter(number -> validator.isValid(type, number));
    }

    @Transactional(readOnly = true)
    public long countAvailable(AccountType type) {
        return repository.countByAccountType(type);
    }

    @Transactional(readOnly = true)
    public boolean hasAvailable(AccountType type) {
        return repository.existsByAccountType(type);
    }
}