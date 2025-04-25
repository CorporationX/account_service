package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;

public interface AccountService {

    AccountResponse getAccount(long id);

    AccountResponse createAccount(AccountRequest accountRequest);

    AccountResponse blockAccount(long id);

    AccountResponse closeAccount(long id);
}
