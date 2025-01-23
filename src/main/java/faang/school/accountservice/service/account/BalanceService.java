package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Balance;
import faang.school.accountservice.mapper.account.BalanceMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.account.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    public BalanceDto getBalanceByAccount(Long accountId) {
        Balance balance = balanceRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account or associated balance doesn't exist"));
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto createBalanceForAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account doesn't exist"));
        Balance savedBalance = balanceRepository.save(new Balance().setAccount(account));
        return balanceMapper.toDto(savedBalance);
    }

    @Transactional
    public BalanceDto updateBalanceForAccount(BalanceDto balanceDto) {
        Balance balance = balanceRepository.findById(balanceDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Balance doesn't exist"));
        balance.setAuthorisationBalance(balanceDto.getAuthorisationBalance());
        balance.setActualBalance(balanceDto.getActualBalance());
        balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }
}
