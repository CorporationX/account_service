package faang.school.accountservice.integration.repository;

import faang.school.accountservice.integration.IntegrationTestBase;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRepositoryIT extends IntegrationTestBase {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountRepositoryIT(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Test
    void findAllByOwnerId() {
        List<Account> result = accountRepository.findAllByOwnerId(1L);
        assertThat(result).isNotEmpty().hasSize(1);
    }
}
