package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.RequestDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.dto.request.RequestUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    @Override
    public RequestResponseDto createRequest(RequestDto requestDto) {
        return null;
    }

    @Override
    public RequestResponseDto updateRequest(RequestUpdateDto requestDto) {
        return null;
    }
}
