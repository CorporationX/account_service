package faang.school.accountservice.service;

import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.enums.RequestStatus;

public interface RequestService {
    public void createRequest(Request request);

    Request updateStatus(Long id, RequestStatus newStatus);

    Request updateFlag(Long id, boolean newFlag);

    Request updateContext(Long id, String newContext);

    Request getRequest(Long id) ;
}
