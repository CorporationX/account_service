package faang.school.accountservice.service;

import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private AccountMapperImpl accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    @Test
    public void testGeyById() {
        accountService.getById(1L);
        verify(accountRepository).getByIdOrThrow(1L);
    }




}
