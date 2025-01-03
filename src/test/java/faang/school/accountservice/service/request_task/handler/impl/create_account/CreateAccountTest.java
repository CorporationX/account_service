package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountOwnerService;
import faang.school.accountservice.service.FreeAccountNumbersService;
import faang.school.accountservice.service.request.RequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountTest {

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private FreeAccountNumbersService numbersService;

    @Mock
    private AccountOwnerService accountOwnerService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RequestService requestService;

    @Mock
    private CheckAccountsQuantity checkAccountsQuantity;

    @InjectMocks
    private CreateAccount createAccount;

    @Test
    public void executeTest() throws JsonProcessingException {
        long ownerId = 1L;
        long accountId = 2L;
        OwnerType ownerType = OwnerType.USER;
        String accountNumber = "4222000000000001";

        AccountOwner accountOwner = AccountOwner.builder()
                .id(ownerId)
                .build();

        AccountRequest accountRequest = AccountRequest.builder()
                .ownerId(ownerId)
                .ownerType(ownerType)
                .type(AccountType.DEBIT)
                .currency(Currency.USD)
                .build();

        RequestTask requestTask1 = RequestTask.builder()
                .status(RequestTaskStatus.AWAITING)
                .handler(RequestTaskType.WRITE_INTO_ACCOUNT)
                .build();

        RequestTask requestTask2 = RequestTask.builder()
                .status(RequestTaskStatus.AWAITING)
                .handler(RequestTaskType.SEND_CREATE_ACCOUNT_NOTIFICATION)
                .build();

        Request request = Request.builder()
                .requestTasks(new ArrayList<>(List.of(requestTask1, requestTask2)))
                .context(objectMapper.writeValueAsString(accountRequest))
                .build();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber(accountNumber)
                .type(accountRequest.getType())
                .currency(accountRequest.getCurrency())
                .status(AccountStatus.ACTIVE)
                .build();

        when(numbersService.getFreeAccountNumber(accountRequest.getType())).thenReturn(accountNumber);
        when(accountOwnerService.findOwner(ownerId, ownerType)).thenReturn(accountOwner);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        createAccount.execute(request);

        RequestTask updatedTask = request.getRequestTasks().stream()
                .filter(task -> task.getHandler().equals(requestTask1.getHandler())).findFirst().get();

        RequestTask notUpdatedTask = request.getRequestTasks().stream()
                .filter(task -> task.getHandler().equals(requestTask2.getHandler())).findFirst().get();

        verify(requestService).updateRequest(request);

        assertEquals(account.getId().toString(), request.getContext());
        assertEquals(RequestTaskStatus.DONE, updatedTask.getStatus());
        assertEquals(RequestTaskStatus.AWAITING, notUpdatedTask.getStatus());
        assertEquals(String.valueOf(accountId), updatedTask.getRollbackContext());
    }

    @Test
    public void executeThrowsJsonProcessingExceptionTest() throws JsonProcessingException {
        long ownerId = 1L;
        OwnerType ownerType = OwnerType.USER;

        RequestTask requestTask1 = RequestTask.builder()
                .status(RequestTaskStatus.AWAITING)
                .handler(RequestTaskType.WRITE_INTO_ACCOUNT)
                .build();

        RequestTask requestTask2 = RequestTask.builder()
                .status(RequestTaskStatus.DONE)
                .handler(RequestTaskType.CHECK_ACCOUNTS_QUANTITY)
                .build();

        AccountRequest accountRequest = AccountRequest.builder()
                .ownerId(ownerId)
                .ownerType(ownerType)
                .type(AccountType.DEBIT)
                .currency(Currency.USD)
                .build();

        Request request = Request.builder()
                .requestTasks(new ArrayList<>(List.of(requestTask1, requestTask2)))
                .context(objectMapper.writeValueAsString(accountRequest))
                .build();

        when(objectMapper.readValue(request.getContext(), AccountRequest.class)).
                thenThrow(JsonProcessingException.class);

        assertThrows(JsonMappingException.class, () -> createAccount.execute(request));
        verify(checkAccountsQuantity).rollback(request);
        verify(accountRepository, times(0)).deleteById(any());
    }

    @Test
    public void executeThrowsOptimisticLockingFailureExceptionTest() throws JsonProcessingException {
        long ownerId = 1L;
        long accountId = 2L;
        OwnerType ownerType = OwnerType.USER;
        String accountNumber = "4222000000000001";

        AccountOwner accountOwner = AccountOwner.builder()
                .id(ownerId)
                .build();

        AccountRequest accountRequest = AccountRequest.builder()
                .ownerId(ownerId)
                .ownerType(ownerType)
                .type(AccountType.DEBIT)
                .currency(Currency.USD)
                .build();

        RequestTask requestTask1 = RequestTask.builder()
                .status(RequestTaskStatus.AWAITING)
                .handler(RequestTaskType.WRITE_INTO_ACCOUNT)
                .build();

        RequestTask requestTask2 = RequestTask.builder()
                .status(RequestTaskStatus.DONE)
                .handler(RequestTaskType.CHECK_ACCOUNTS_QUANTITY)
                .build();

        Request request = Request.builder()
                .requestTasks(new ArrayList<>(List.of(requestTask1, requestTask2)))
                .context(objectMapper.writeValueAsString(accountRequest))
                .build();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber(accountNumber)
                .type(accountRequest.getType())
                .currency(accountRequest.getCurrency())
                .status(AccountStatus.ACTIVE)
                .build();

        when(numbersService.getFreeAccountNumber(accountRequest.getType())).thenReturn(accountNumber);
        when(accountOwnerService.findOwner(ownerId, ownerType)).thenReturn(accountOwner);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        doThrow(OptimisticLockingFailureException.class).when(requestService).updateRequest(any());

        assertThrows(OptimisticLockingFailureException.class, () -> createAccount.execute(request));

        verify(checkAccountsQuantity).rollback(request);
        verify(accountRepository, times(1)).deleteById(any());
    }

    @Test
    public void rollbackTest(){
        RequestTask requestTask1 = RequestTask.builder()
                .status(RequestTaskStatus.DONE)
                .handler(RequestTaskType.WRITE_INTO_ACCOUNT)
                .rollbackContext("1")
                .build();

        RequestTask requestTask2 = RequestTask.builder()
                .status(RequestTaskStatus.DONE)
                .handler(RequestTaskType.CHECK_ACCOUNTS_QUANTITY)
                .build();

        Request request = Request.builder()
                .requestStatus(RequestStatus.PROCESSING)
                .requestTasks(new ArrayList<>(List.of(requestTask1, requestTask2)))
                .build();

        createAccount.rollback(request);

        verify(accountRepository).deleteById(Long.getLong(requestTask1.getRollbackContext()));
        verify(checkAccountsQuantity).rollback(request);
        assertEquals(RequestTaskStatus.AWAITING,requestTask1.getStatus());
    }
}