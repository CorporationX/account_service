package faang.school.accountservice.controller;

import faang.school.accountservice.enums.PaymentMessageType;
import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/create")
    public ResponseEntity<Request> createRequest(@RequestBody PaymentMessageDto message,
                                                 @RequestParam PaymentMessageType requestType,
                                                 @RequestParam(required = false) String lockValue) {
        Request request = requestService.createRequest(message, requestType, lockValue);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Request> updateStatus(@PathVariable("id") UUID requestId,
                                                @RequestParam PaymentStages newStatus,
                                                @RequestParam(required = false) String statusDetails) {
        Request updated = requestService.updateStatus(requestId, newStatus, statusDetails);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequest(@PathVariable("id") UUID requestId) {
        Request request = requestService.getRequest(requestId);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Request>> findPendingRequests() {
        List<Request> pending = requestService.findPendingRequestsToClear();
        return ResponseEntity.ok(pending);
    }
}