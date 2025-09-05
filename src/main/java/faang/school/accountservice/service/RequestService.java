package faang.school.accountservice.service;

import faang.school.accountservice.enums.PaymentMessageType;
import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.dto.PaymentMessageDto;

import java.util.List;
import java.util.UUID;

public interface RequestService {

    Request createRequest(PaymentMessageDto message, PaymentMessageType requestType, String lockValue);

    Request updateStatus(UUID requestId, PaymentStages newStatus, String statusDetails);

    Request getRequest(UUID requestId);

    List<Request> findPendingRequestsToClear();
}