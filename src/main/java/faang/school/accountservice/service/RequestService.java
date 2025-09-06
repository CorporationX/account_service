package faang.school.accountservice.service;

import faang.school.accountservice.enums.PaymentMessageType;
import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.model.dto.RequestDto;

import java.util.List;
import java.util.UUID;

public interface RequestService {
    RequestDto createRequest(PaymentMessageDto message, PaymentMessageType requestType, String lockValue);

    RequestDto updateStatus(UUID requestId, PaymentStages newStatus, String statusDetails);

    RequestDto getRequest(UUID requestId);

    List<RequestDto> findPendingRequestsToClear();

    RequestDto createRequestNewTransaction(PaymentMessageDto message, PaymentMessageType requestType, String lockValue);
}