package faang.school.accountservice.controller;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestInfoDto;
import faang.school.accountservice.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/request")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestInfoDto> createRequest(@Valid @RequestBody RequestCreateDto dto) {
        return ResponseEntity.ok(requestService.createRequest(dto));
    }

    @GetMapping("/{idempotencyKey}")
    public ResponseEntity<RequestInfoDto> getRequest(@PathVariable UUID idempotencyKey) throws AccessDeniedException {
        return ResponseEntity.ok(requestService.getRequest(idempotencyKey));
    }

    @DeleteMapping("/{idempotencyKey}")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID idempotencyKey) throws AccessDeniedException {
        requestService.deleteRequest(idempotencyKey);
        return ResponseEntity.noContent().build();
    }
}
