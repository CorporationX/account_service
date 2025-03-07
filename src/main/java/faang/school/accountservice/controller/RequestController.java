package faang.school.accountservice.controller;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestReadDto;
import faang.school.accountservice.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestReadDto create(@RequestBody @Valid RequestCreateDto dto) {
        return requestService.createRequest(dto);
    }

    @PatchMapping("/{idempotentKey}/start")
    public RequestReadDto start(@PathVariable String idempotentKey) {
        return requestService.startRequest(idempotentKey);
    }

    @PatchMapping("/{idempotentKey}/close")
    public RequestReadDto close(@PathVariable String idempotentKey) {
        return requestService.closeRequest(idempotentKey);
    }
}
