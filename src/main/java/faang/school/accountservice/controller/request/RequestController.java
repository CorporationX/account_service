package faang.school.accountservice.controller.request;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.service.request.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDto> createRequest(@RequestBody @Valid RequestCreateDto dto) {
        RequestResponseDto response = requestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeRequest(@PathVariable UUID id) {
        Request request = requestService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос не найден"));

        requestService.completeRequest(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/storage")
    public ResponseEntity<Void> updateStorage(@PathVariable UUID id, @RequestBody Map<String, Object> storage) {
        Request request = requestService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос не найден"));

        requestService.updateStorage(request, storage);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id,
                                             @RequestParam RequestStatus status,
                                             @RequestParam(required = false) String details) {
        Request request = requestService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос не найден"));

        requestService.updateStatus(request, status, details);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<RequestResponseDto>> getByStatus(@RequestParam RequestStatus status) {
        return ResponseEntity.ok(requestService.getByStatus(status));
    }
}
