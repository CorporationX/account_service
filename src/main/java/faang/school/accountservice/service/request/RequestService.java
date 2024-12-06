package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.account.RequestDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.entity.request.RequestStatus;
import faang.school.accountservice.repository.request.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public void startProcessCreateAccount(List<Request> requests) {
        requests.forEach(request -> Request.builder().requestStatus(RequestStatus.SUCCESS).build());
        requestRepository.saveAll(requests);
    }
    public void saveRequests(List<Request> requests) {
        requestRepository.saveAll(requests);
    }

    public void saveRequest(RequestDto requestDto) {
        Request request = Request.builder()
                .account(Account.builder()
                        .id(requestDto.getAccountCreateDto().getId())
                        .build())
                .balance(Balance.builder()
                        .id(requestDto.getAccountCreateDto().getId())
                        .build())
                .owner(Owner.builder()
                        .id(requestDto.getAccountCreateDto().getId())
                        .build())
                .requestStatus(requestDto.getRequestStatus())
                .context(requestDto.getContext())
                .scheduledAt(requestDto.getScheduledAt())
                .build();
        requestRepository.save(request);
    }

    public List<Request> findRequestScheduled() {
       return requestRepository.findScheduledAt();
    }
}
