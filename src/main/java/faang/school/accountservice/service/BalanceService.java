package faang.school.accountservice.service;

import faang.school.accountservice.dto.AuthorizationEvent;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.enums.BalanceStatus;
import faang.school.accountservice.mappers.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.exception.BalanceConflictException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    //добавляем для инжектирования EntityManager который поможет сделать синхронизацию с бд
    //если не добавим обнавлятся сумма в бд не будет
    @PersistenceContext
    private EntityManager entityManager;

    public BalanceDto getBalanceByAccountId(Long accountId) {
        log.info("Getting balance by account id {}", accountId);
        Balance balance = balanceRepository.findById(accountId)
                .orElseThrow(() -> new BalanceNotFoundException("Balance not found for account ID: " + accountId));
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto createBalance(BalanceDto balanceDto) {
        log.info("Creating balance {}", balanceDto);
        Balance balance = balanceMapper.toEntity(balanceDto);
        balance = balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto updateBalance(Long accountId, BalanceDto balanceDto) {
        log.info("Updating balance {}", balanceDto);
        Balance balance = balanceRepository.findById(accountId)
                .orElseThrow(() -> new BalanceNotFoundException("Balance not found for account ID: " + accountId));

        balance.setAuthBalance(balanceDto.getAuthBalance());
        balance.setActualBalance(balanceDto.getActualBalance());

        try {
            balance = balanceRepository.save(balance);
        } catch (OptimisticLockException e) {
            log.warn("Conflict during balance update. Try again later.");
            throw new BalanceConflictException("Conflict during balance update. Try again later.", e);
        }

        return balanceMapper.toDto(balance);
    }

    /**
     * Метод добавляет зарезервированную сумму
     * **/
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void authorizePayment(Account account, BigDecimal amount) {
        Balance balance = account.getBalance();

        // Обновляем actualBalance
        BigDecimal updatedAuthBalance = balance.getAuthBalance().add(amount);
        balance.setAuthBalance(updatedAuthBalance);

        // Устанавливаем статус баланса
        balance.setStatus(BalanceStatus.PENDING);

        // Сохраняем изменения
        balanceRepository.save(balance);

        // Принудительно синхронизируем с базой данных
        entityManager.flush();

        log.info("Successfully authorized payment for account ID: {}", account.getId());
    }


    @Transactional
    public void clearingPayment(Long senderBalanceId, Long recipientBalanceId, BigDecimal amount) {
        Balance senderBalance = balanceRepository.findById(senderBalanceId).orElseThrow(
                () -> new RuntimeException("Sender balance not found"));
        Balance recipientBalance = balanceRepository.findById(recipientBalanceId).orElseThrow(
                () -> new RuntimeException("Recipient balance not found"));


        // начисление получателю
        BigDecimal updatedRecipientBalance = recipientBalance.getActualBalance().add(amount);
        recipientBalance.setActualBalance(updatedRecipientBalance);
        recipientBalance.setStatus(BalanceStatus.APPROVED);

        // списываем со счета у покупателя
        BigDecimal updatedSenderActualBalance = senderBalance.getActualBalance().subtract(amount);
        BigDecimal updatedSenderAuthBalance = senderBalance.getAuthBalance().subtract(amount);
        senderBalance.setActualBalance(updatedSenderActualBalance);
        senderBalance.setAuthBalance(updatedSenderAuthBalance);
        senderBalance.setStatus(BalanceStatus.APPROVED);


        balanceRepository.save(recipientBalance);
        balanceRepository.save(senderBalance);

    }
}