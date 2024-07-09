package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffRateChangeRequestDto;
import faang.school.accountservice.model.TariffRateChangeRequest;
import faang.school.accountservice.service.TariffRateChangeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tariff-rate-changes")
@RequiredArgsConstructor
public class TariffRateChangeRequestController {
    private final TariffRateChangeRequestService tariffRateChangeRequestService;

    @PostMapping
    public ResponseEntity<TariffRateChangeRequestDto> requestTariffRateChange(@RequestBody @Valid TariffRateChangeRequest request){
        TariffRateChangeRequestDto savedRequest = tariffRateChangeRequestService.requestTariffRateChange(request);
        return ResponseEntity.ok(savedRequest);
    }
}
