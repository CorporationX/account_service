package faang.school.accountservice.service.request_task.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.RequestTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateBalanceAndBalanceAudit implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final BalanceService balanceService;
    private final RequestService requestService;

    @Override
    public void execute(Request request) {
        Account account;
        try {
            account = objectMapper.readValue(request.getContext(), Account.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        balanceService.createBalance(account);
        request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getHandler().
                        equals(RequestTaskType.WRITE_INTO_BALANCE_BALANCE_AUDIT))
                .forEach(requestTask -> requestTask.setStatus(RequestTaskStatus.DONE));
        requestService.updateRequest(request);
    }

    @Override
    public long getHandlerId() {
        return 3;
    }
}
