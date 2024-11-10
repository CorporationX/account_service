package faang.school.accountservice.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestTaskRepository;


public class AccountAmountHandler implements RequestTaskHandler {
    private RequestTaskRepository requestTaskRepository;

    @Override
    public void execute(Request request, RequestTask task) {
// отсутсвует UserClient, поэтому проверить кол-во счетов невозможно
        task.setRequestTaskStatus(RequestStatus.COMPLETED);
        requestTaskRepository.save(task);
    }

    @Override
    public Long getHandlerId() {
        return 1L;
    }
}
