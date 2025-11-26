package faang.school.accountservice.controller;

import faang.school.accountservice.dto.RequestCreateDto;
import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.dto.RequestUpdateContextDto;
import faang.school.accountservice.dto.RequestUpdateFlagDto;
import faang.school.accountservice.dto.RequestUpdateStatusDto;
import faang.school.accountservice.service.RequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestResponseDto createRequest(@Valid @RequestBody RequestCreateDto requestCreateDto) {
        return requestService.createRequest(requestCreateDto);
    }

    @PatchMapping("/{id}/status")
    public RequestResponseDto updateStatus(
            @PathVariable("id") @Positive long id,
            @Valid @RequestBody RequestUpdateStatusDto statusDto
    ) {
        return requestService.updateStatusRequest(id, statusDto);
    }

    @PatchMapping("/{id}/flag")
    public RequestResponseDto updateFlag(
            @PathVariable("id") @Positive long id,
            @Valid @RequestBody RequestUpdateFlagDto flagDto
    ) {
        return requestService.updateFlagRequest(id, flagDto);
    }

    @PatchMapping("/{id}/context")
    public RequestResponseDto updateContext(
            @PathVariable("id") @Positive long id,
            @Valid @RequestBody RequestUpdateContextDto contextDto
    ) {
        return requestService.updateContextRequest(id, contextDto);
    }
}
