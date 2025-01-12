package faang.school.accountservice.controller.request;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.service.RequestExecutorService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/requests")
public class RequestController {
    private final RequestExecutorService requestExecutorService;

    @GetMapping("/{id}/status")
    public ResponseEntity<RequestStatus> getRequestStatus(@PathVariable("id") @NotNull @Positive UUID id) {
        RequestStatus status = requestExecutorService.getRequestStatus(id);
        return ResponseEntity.ok(status);
    }
}
