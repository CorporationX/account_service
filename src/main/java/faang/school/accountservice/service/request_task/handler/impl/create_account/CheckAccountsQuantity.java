package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.service.AccountOwnerService;
import faang.school.accountservice.service.RequestService;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckAccountsQuantity implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final AccountOwnerService accountOwnerService;
    private final RequestService requestService;

    @Value("${account.max-accounts-quantity}")
    private int maxAccountsQuantity;

    @Override
    public void execute(Request request) {
        AccountRequest accountRequest;
        try {
            accountRequest = objectMapper.readValue(request.getContext(), AccountRequest.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException("Error processing Json");
        }

        AccountOwner owner = accountOwnerService.
                findOwner(accountRequest.getOwnerId(), accountRequest.getOwnerType());

        if (owner.getAccounts().size() >= maxAccountsQuantity) {
            throw new IllegalStateException("Forbidden have more than " +
                    maxAccountsQuantity + " accounts");
        }
        request.setRequestStatus(RequestStatus.PROCESSING);
        requestService.updateRequest(request);
    }

    @Override
    public long getHandlerId() {
        return 1;
    }
}
