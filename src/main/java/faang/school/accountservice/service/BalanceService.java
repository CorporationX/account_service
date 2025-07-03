package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountService accountService;
    private final BalanceMapper balanceMapper;

    private Balance getBalanceByIdForUpdate(Long balanceId) {
        return balanceRepository.findByIdForUpdate(balanceId).orElseThrow(() ->
                new EntityNotFoundException("Balance with id = " + balanceId + "not found"));
    }

    public BalanceDto getBalanceById(Long balanceId) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow(() ->
                new EntityNotFoundException("Balance with id = " + balanceId + "not found"));
        return balanceMapper.toDto(balance);
    }


    @Transactional
    public BalanceDto createBalance(Long accountId) {
        Balance balance = Balance.builder()
                .account(accountService.getAccountById(accountId))
                .build();

        balanceRepository.save(balance);

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto plusBalance(Long balanceId, Double money) {
        Balance balance = getBalanceByIdForUpdate(balanceId);
        balance.setActualBalance(balance.getActualBalance() + money);

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto authBalance(Long balanceId, Double money) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        if (balance.getAuthBalance() != 0.) {
            throw new IllegalArgumentException("There is already money reserved");
        }

        balance.setActualBalance(balance.getActualBalance() - money);
        balance.setAuthBalance(money);

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto clearingBalanceAllSum(Long balanceId) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        if (balance.getAuthBalance() == 0.) {
            throw new IllegalArgumentException("Auth Balance is 0");
        }

        balance.setAuthBalance(0.);

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto clearingBalancePartSum(Long balanceId, Double sum) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        if (balance.getAuthBalance() == 0.) {
            throw new IllegalArgumentException("Auth Balance is 0");
        }

        balance.setActualBalance(balance.getActualBalance() + balance.getAuthBalance() - sum);
        balance.setAuthBalance(0.);

        return balanceMapper.toDto(balance);
    }


    @Transactional
    public BalanceDto cancelBalance(Long balanceId) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        if (balance.getAuthBalance() == 0.) {
            throw new IllegalArgumentException("Cancellation is not possible");
        }

        balance.setActualBalance(balance.getActualBalance() + balance.getAuthBalance());
        balance.setAuthBalance(0.);

        return balanceMapper.toDto(balance);
    }
}
