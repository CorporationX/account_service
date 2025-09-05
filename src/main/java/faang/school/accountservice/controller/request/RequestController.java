package faang.school.accountservice.controller.request;


import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.enums.RequestStatus;
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

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<Request> createRequest(@RequestBody Request request) {
        Request created = requestService.createRequest(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{idpToken}")
    public ResponseEntity<Request> getRequestById(@PathVariable UUID idpToken) {
        Request request = requestService.findById(idpToken)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{idpToken}/status")
    public ResponseEntity<Request> updateStatus(
            @PathVariable UUID idpToken,
            @RequestParam RequestStatus status) {
        requestService.updateStatus(idpToken, status);
        return ResponseEntity.ok(requestService.findById(idpToken).orElseThrow());
    }

    @PutMapping("/{idpToken}/context")
    public ResponseEntity<Request> updateContext(
            @PathVariable UUID idpToken,
            @RequestBody Map<String, Object> context) {
        requestService.updateContext(idpToken, context);
        return ResponseEntity.ok(requestService.findById(idpToken).orElseThrow());
    }
}

