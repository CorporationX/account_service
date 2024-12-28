package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountOwnerService;
import faang.school.accountservice.service.FreeAccountNumbersService;
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAccount implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final FreeAccountNumbersService numbersService;
    private final AccountOwnerService accountOwnerService;
    private final AccountRepository accountRepository;
    private final RequestService requestService;

    @Override
    public void execute(Request request) {
        String executedContext;
        try {
            AccountRequest accountRequest = objectMapper.
                    readValue(request.getContext(), AccountRequest.class);
            String number = numbersService.getFreeAccountNumber(accountRequest.getType());
            AccountOwner owner = accountOwnerService.findOwner(accountRequest.getOwnerId(),
                    accountRequest.getOwnerType());

            Account account = Account.builder()
                    .accountNumber(number)
                    .type(accountRequest.getType())
                    .currency(accountRequest.getCurrency())
                    .status(AccountStatus.ACTIVE)
                    .owner(owner)
                    .build();

            accountRepository.save(account);
            executedContext = objectMapper.writeValueAsString(account);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        request.setContext(executedContext);
        request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getHandler().
                        equals(RequestTaskType.WRITE_INTO_ACCOUNT))
                .forEach(requestTask -> requestTask.setStatus(RequestTaskStatus.DONE));
        requestService.updateRequest(request);
        log.info("Finished processing request task with type: {}",RequestTaskType.WRITE_INTO_ACCOUNT);
    }

    @Override
    public long getHandlerId() {
        return 2;
    }
}
