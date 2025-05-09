package faang.school.accountservice.controller;

import faang.school.accountservice.dto.Request.RequestDto;
import faang.school.accountservice.dto.Request.RequestStatusDto;
import faang.school.accountservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public String create(@RequestBody RequestDto requestDto) {
        UUID idempotencyToken = UUID.randomUUID();
        return requestService.createRequest(requestDto, idempotencyToken);
    }

    @PutMapping("/status/{requestId}")
    public RequestDto updateStatus(@PathVariable Long requestId,@RequestBody RequestStatusDto requestStatusDto) {
        return requestService.updateRequestStatus(requestId, requestStatusDto);
    }

    @PutMapping("/flag/{requestId}")
    public RequestDto updateFlag(@PathVariable Long requestId,@RequestParam boolean flag) {
        return requestService.updateIsOpenFlag(requestId, flag);
    }

    @PutMapping("/input-data/{requestId}")
    public RequestDto updateInputData(@PathVariable Long requestId, @RequestBody Map<String, Object> inputData) {
        return requestService.updateInputData(requestId, inputData);
    }
}
