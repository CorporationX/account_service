package faang.school.accountservice.controller;

import faang.school.accountservice.dto.RequestCreateDto;
import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.dto.RequestUpdateContextDto;
import faang.school.accountservice.dto.RequestUpdateFlagDto;
import faang.school.accountservice.dto.RequestUpdateStatusDto;
import faang.school.accountservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDto> createRequest(@RequestBody RequestCreateDto requestCreateDto) {
        RequestResponseDto response = requestService.createRequest(requestCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RequestResponseDto> updateStatus(
            @PathVariable("id") long id,
            @RequestBody RequestUpdateStatusDto statusDto
    ) {
        RequestResponseDto response = requestService.updateStatusRequest(id, statusDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/flag")
    public ResponseEntity<RequestResponseDto> updateFlag(
            @PathVariable("id") long id,
            @RequestBody RequestUpdateFlagDto flagDto
    ) {
        RequestResponseDto response = requestService.updateFlagRequest(id, flagDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/context")
    public ResponseEntity<RequestResponseDto> updateContext(
            @PathVariable("id") long id,
            @RequestBody RequestUpdateContextDto contextDto
    ) {
        RequestResponseDto response = requestService.updateContextRequest(id, contextDto);
        return ResponseEntity.ok(response);
    }
}
