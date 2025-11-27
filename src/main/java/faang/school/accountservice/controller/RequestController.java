package faang.school.accountservice.controller;

import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.dto.request.UpdateRequestDto;
import faang.school.accountservice.service.request.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseRequestDto createRequest(@RequestHeader("Idempotency-Token") UUID idempotencyToken,
                                            @Valid @RequestBody CreateRequestDto dto) {
        return requestService.createRequest(idempotencyToken, dto);
    }

    @PatchMapping("/{idempotencyToken}")
    public ResponseRequestDto updateRequestStatus(@PathVariable UUID idempotencyToken,
                                                  @RequestBody UpdateRequestDto dto) {
        return requestService.updateRequestStatus(idempotencyToken, dto.status(), dto.statusDetails());
    }
}
