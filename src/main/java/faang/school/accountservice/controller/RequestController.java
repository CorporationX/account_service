package faang.school.accountservice.controller;

import faang.school.accountservice.dto.request.RequestCreationDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.dto.request.RequestUpdateDto;
import faang.school.accountservice.service.request.RequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDto> createRequest(
            @Valid @NotNull @RequestBody RequestCreationDto requestCreationDto) {

        log.info("Received request to create request {}", requestCreationDto);
        return ResponseEntity.ok(requestService.createRequest(requestCreationDto));
    }

    @PatchMapping
    public ResponseEntity<RequestResponseDto> updateRequest(
            @Valid @NotNull @RequestBody RequestUpdateDto requestUpdateDto) {

        log.info("Received request to update request {}", requestUpdateDto);
        return ResponseEntity.ok(requestService.updateRequest(requestUpdateDto));
    }
}
