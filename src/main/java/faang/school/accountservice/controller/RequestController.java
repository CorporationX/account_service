package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.dto.UpdateStatusDto;
import faang.school.accountservice.service.RequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/request")
public class RequestController {
    private final RequestService service;

    @PostMapping
    public ResponseEntity<RequestDto> create(@RequestBody @Valid CreateRequestDto createRequestDto) {
        RequestDto requestDto = service.createRequest(createRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(requestDto);
    }

    @PutMapping
    public void updateStatus(@RequestBody @Valid UpdateStatusDto updateStatusDto) {
        service.updateStatus(updateStatusDto);
    }

    @PostMapping("/{idempotencyKey}/close")
    public void closeRequest(@RequestBody @NotNull UUID idempotencyKey) {
        service.closeRequest(idempotencyKey);
    }
}
