package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping()
    public ResponseEntity<Void> createRequest(@Valid @RequestBody CreateRequestDto createRequestDto) {
        requestService.createRequest(createRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("{token}/status/{status}")
    public void updateRequestStatusByToken(@PathVariable("token") UUID requestToken,
            @PathVariable("status") RequestStatus newRequestStatus) {
        requestService.updateRequestStatusByToken(requestToken, newRequestStatus);
    }

    @PatchMapping("{token}/body")
    public void updateRequestBodyByToken(@PathVariable("token") UUID requestToken,
            @RequestBody Map<String, Object> newBody) {
        requestService.updateRequestBodyByToken(requestToken, newBody);
    }
}
