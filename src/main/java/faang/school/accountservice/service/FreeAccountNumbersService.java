package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.InvoiceType;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    public void addFreeAccountNumber(InvoiceType invoiceType, String accountNumber) {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(new FreeAccountNumberId(invoiceType, accountNumber))
                .build();
        freeAccountNumbersRepository.save(freeAccountNumber);
    }

    @PostConstruct
    public void initAccountCounters() {
        for (InvoiceType invoiceType : InvoiceType.values()) {
            createCounterForAccountType(invoiceType);
        }
    }

    public AccountNumberSequence createCounterForAccountType(InvoiceType invoiceType) {
        return accountNumbersSequenceRepository.findByInvoiceType(invoiceType)
                .orElseGet(() -> accountNumbersSequenceRepository
                        .save(AccountNumberSequence.builder()
                                .invoiceType(invoiceType)
                                .currentCounter(0L)
                                .updateAt(LocalDateTime.now())
                                .build())
                );
    }

    public boolean incrementCounterIfMatches(InvoiceType invoiceType, long expectedValue) {
        Optional<Long> newCounterValue = accountNumbersSequenceRepository
                .incrementCounter(invoiceType.name(), expectedValue);
        return newCounterValue.isPresent();
    }

    @Transactional
    public void executeWithNewAccountNumber(InvoiceType invoiceType, Consumer<String> executeLambda) {
        Optional<String> freeAccountNumber = freeAccountNumbersRepository
                .getAndDeleteFirstFreeAccountNumber(invoiceType.name())
                .map(FreeAccountNumberId::getAccountNumber);

        freeAccountNumber.ifPresentOrElse(executeLambda,
                () -> {
                    String newAccountNumber = incrementAndGet(invoiceType);
                    executeLambda.accept(newAccountNumber);
                });
    }

    @Transactional
    private String incrementAndGet(InvoiceType invoiceType) {
        AccountNumberSequence accountNumber = accountNumbersSequenceRepository
                .findByInvoiceType(invoiceType)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Счетчик для типа счета: %s не найден", invoiceType.name())));
        accountNumbersSequenceRepository
                .incrementCounter(invoiceType.name(), accountNumber.getCurrentCounter());
        FreeAccountNumberId freeAccountNumberId = freeAccountNumbersRepository
                .getAndDeleteFirstFreeAccountNumber(invoiceType.name())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Свободный номер для типа счета: %s не найден", invoiceType.name())));

        return freeAccountNumberId.getAccountNumber();
    }
}
