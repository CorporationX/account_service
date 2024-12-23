package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.account.AccountService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final AccountService accountService;

    @Transactional
    @Override
    public BalanceDto create(Long userId, BalanceCreateDto balanceCreateDto) {
        validateUser(userId);
        long accountId = balanceCreateDto.accountId();
        accountService.getAccount(accountId);
        Balance balance = balanceRepository.create(accountId,
                balanceCreateDto.authorizedValue());
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    @Override
    public BalanceDto update(Long userId, PaymentDto paymentDto) {
        validateUser(userId);
        Long id = paymentDto.balanceId();
        BigDecimal value = paymentDto.value();
        Balance balance = findBalanceById(id);

        switch (paymentDto.paymentOperationType()) {
            case INITIATE -> balance.authorizePayment(value);
            case CANCEL -> balance.cancelAuthorization(value);
            case CONFIRM -> balance.clearPayment(value);
            default -> throw new IllegalArgumentException("Wrong payment step");
        }
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Override
    public Balance findBalanceById(Long id) {
        return balanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("balance not found, id = " + id));
    }

    @Override
    public BalanceDto getBalanceById(Long userId, Long BalanceId) {
        validateUser(userId);
        return balanceMapper.toDto(findBalanceById(BalanceId));
    }

    private void validateUser(Long userId) {
        log.info("user with id = {} validated", userId);
    }
}