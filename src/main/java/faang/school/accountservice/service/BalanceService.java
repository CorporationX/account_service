package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.Account;
import faang.school.accountservice.dto.balance.BalanceCreateRequestDto;
import faang.school.accountservice.dto.balance.BalanceCreateResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateRequestDto;
import faang.school.accountservice.dto.balance.BalanceUpdateResponseDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {
    private final AccountService accountService;
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    public BalanceCreateResponseDto createBalance(BalanceCreateRequestDto balanceCreateRequestDto) {
        String accountNumber = balanceCreateRequestDto.getAccountNumber();
        log.info("Start creating account with number: {}", accountNumber);
        Account account = accountService.getAccountByNumber(accountNumber);
        Balance balance = Balance.builder()
                .account(account)
                .authorisationBalance(BigDecimal.ZERO)
                .factualBalance(BigDecimal.ZERO)
                .build();
        Balance savedBalance = balanceRepository.save(balance);
        log.info("Saved account balance with account number {} and id {}",
                savedBalance.getAccount().getAccountNumber(),
                savedBalance.getId());
        return balanceMapper.toBalanceCreateResponseDto(savedBalance);
    }

    public BalanceUpdateResponseDto updateBalance(BalanceUpdateRequestDto balanceUpdateRequestDto) {
        log.info("Start updating balance with id {}", balanceUpdateRequestDto.getId());
        Balance balance = getBalanceById(balanceUpdateRequestDto.getId());
        balanceMapper.updateBalance(balanceUpdateRequestDto, balance);
        Balance savedBalance = balanceRepository.save(balance);
        log.info("Balance with id {} updated", savedBalance.getId());
        return balanceMapper.toBalanceUpdateResponseDto(savedBalance);
    }

    public Balance getBalanceById(Long id) {
        Balance balance = balanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Balance with id " + id + " not found"));
        log.info("Balance with id {} found", id);
        return balance;
    }
}
