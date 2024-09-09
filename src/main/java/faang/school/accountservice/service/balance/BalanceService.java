package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.validator.balance.BalanceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceValidator balanceValidator;
    private final AccountRepository accountRepository;

    public BalanceDto create(BalanceDto balanceDto) {
        Balance balance = balanceMapper.toEntity(balanceDto);
        Account account = accountRepository.findById(balanceDto.getAccountId()).get();
        balance.setAccount(account);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }


    public BalanceDto update(Long id, BalanceDto balanceDto) {
        balanceValidator.updateBalanceValidator(id);
        Balance balance = balanceMapper.toEntity(balanceDto);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }
}
