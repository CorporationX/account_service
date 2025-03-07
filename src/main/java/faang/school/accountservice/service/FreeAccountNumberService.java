package faang.school.accountservice.service;

import faang.school.accountservice.entity.account.AccountNumberSequence;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.entity.account.FreeAccountNumberId;
import faang.school.accountservice.enums.InvoiceType;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;
    private final FreeAccountNumberRepository freeAccountNumberRepository;

    public void addFreeAccountNumber(InvoiceType invoiceType, String accountNumber) {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(new FreeAccountNumberId(invoiceType, accountNumber))
                .build();
        freeAccountNumberRepository.save(freeAccountNumber);
    }

    @PostConstruct
    public void initAccountCounters() {
        for (InvoiceType invoiceType : InvoiceType.values()) {
            createCounterForAccountType(invoiceType);
        }
    }

    public AccountNumberSequence createCounterForAccountType(InvoiceType invoiceType) {
        return accountNumberSequenceRepository.findByInvoiceType(invoiceType)
                .orElseGet(() -> accountNumberSequenceRepository
                        .save(AccountNumberSequence.builder()
                                .invoiceType(invoiceType)
                                .currentCounter(0L)
                                .updateAt(LocalDateTime.now())
                                .build())
                );
    }

    public boolean incrementCounterIfMatches(InvoiceType invoiceType, long expectedValue) {
        Optional<Long> newCounterValue = accountNumberSequenceRepository
                .incrementCounter(invoiceType.name(), expectedValue);
        return newCounterValue.isPresent();
    }

    @Transactional
    public void executeWithNewAccountNumber(InvoiceType invoiceType, Consumer<String> executeLambda) {
        freeAccountNumberRepository
                .getAndDeleteFirstFreeAccountNumber(invoiceType.name())
                .map(FreeAccountNumberId::getAccountNumber)
                .ifPresentOrElse(executeLambda,
                        () -> executeLambda.accept(incrementAndGet(invoiceType)));
    }

    @Transactional
    public String incrementAndGet(InvoiceType invoiceType) {
        AccountNumberSequence accountNumber = accountNumberSequenceRepository
                .findByInvoiceType(invoiceType)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Счетчик для типа счета: %s не найден", invoiceType.name())));

        accountNumberSequenceRepository
                .incrementCounter(invoiceType.name(), accountNumber.getCurrentCounter());

        FreeAccountNumberId freeAccountNumberId = freeAccountNumberRepository
                .getAndDeleteFirstFreeAccountNumber(invoiceType.name())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Свободный номер для типа счета: %s не найден", invoiceType.name())));

        return freeAccountNumberId.getAccountNumber();
    }
}
