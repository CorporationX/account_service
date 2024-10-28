package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
//    private final SavingsAccountRepository savingsAccountRepository;

    @Transactional
    @Override
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
//        Account account = accountRepository.findById(savingsAccountDto.getAccountId()).orElseGet(() -> {
//            log.info("Account with id {} not found", savingsAccountDto.getAccountId());
//            throw new EntityNotFoundException("Account with id " + savingsAccountDto.getAccountId() + " not found");
//        });
//
//        SavingsAccount savingsAccount = SavingsAccount.builder().account(account).build();
//
//        return savingsAccountMapper.savingsAccountToSavingsAccountDto(savingsAccountRepository.save(savingsAccount));
        return null;
    }
}
