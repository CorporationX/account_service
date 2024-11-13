package faang.school.accountservice.controller;

import faang.school.accountservice.dto.cashback.CashbackMerchantDto;
import faang.school.accountservice.dto.cashback.CashbackOperationTypeDto;
import faang.school.accountservice.dto.cashback.CashbackTariffResponseDto;
import faang.school.accountservice.dto.cashback.CashbackTariffShortResponseDto;
import faang.school.accountservice.dto.cashback.CreateCashbackMerchantDto;
import faang.school.accountservice.dto.cashback.CreateCashbackOperationTypeDto;
import faang.school.accountservice.dto.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.dto.cashback.UpdateCashbackMerchantDto;
import faang.school.accountservice.dto.cashback.UpdateCashbackOperationTypeDto;
import faang.school.accountservice.dto.cashback.UpdateCashbackTariffDto;
import faang.school.accountservice.entity.cacheback.CashbackMerchant;
import faang.school.accountservice.entity.cacheback.CashbackOperationType;
import faang.school.accountservice.entity.cacheback.CashbackTariff;
import faang.school.accountservice.mapper.CashbackMapper;
import faang.school.accountservice.service.cashback.CashbackTariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/cashback")
@RestController
public class CashbackTariffController {
    private final CashbackTariffService cashbackTariffService;
    private final CashbackMapper mapper;

    @PostMapping("/tariff")
    public CashbackTariffShortResponseDto createTariff(@RequestBody @Valid CreateCashbackTariffDto dto) {
        CashbackTariff cashbackTariff = mapper.toCashbackTariffEntity(dto);
        CashbackTariff result = cashbackTariffService.createTariff(cashbackTariff);

        return mapper.toShortResponseDto(result);
    }

    @GetMapping("/{id}/tariff")
    public CashbackTariffResponseDto getTariff(@PathVariable UUID id) {
        CashbackTariff result = cashbackTariffService.getTariff(id);

        return mapper.toResponseDto(result);
    }

    @PatchMapping("/{id}/tariff")
    public CashbackTariffResponseDto updateTariff(@PathVariable UUID id,
                                                  @RequestBody @Valid UpdateCashbackTariffDto dto) {
        CashbackTariff cashbackTariff = mapper.toCashbackTariffEntity(dto);
        CashbackTariff result = cashbackTariffService.updateTariff(id, cashbackTariff);

        return mapper.toResponseDto(result);
    }

    @DeleteMapping("/{id}/tariff")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCashbackTariff(@PathVariable UUID id) {
        cashbackTariffService.removeCashbackTariff(id);
    }

    @GetMapping("/tariffs")
    public List<CashbackTariffShortResponseDto> getTariffs() {
        List<CashbackTariff> result = cashbackTariffService.getTariffs();

        return result.stream().map(mapper::toShortResponseDto).toList();
    }

    @PostMapping("/{tariffId}/tariff/attach-operation")
    public CashbackOperationTypeDto addOperationCashback(@PathVariable UUID tariffId,
                                                         @RequestBody @Valid CreateCashbackOperationTypeDto dto) {
        CashbackOperationType cashbackOperationType = mapper.toCashbackOperationType(dto);
        CashbackOperationType result = cashbackTariffService.attachOperationType(tariffId, cashbackOperationType);

        return mapper.toCashbackOperationTypeDto(result);
    }

    @PutMapping("/{id}/operation-type")
    public CashbackOperationTypeDto updateOperationCashback(@PathVariable UUID id,
                                                            @RequestBody @Valid UpdateCashbackOperationTypeDto dto) {
        CashbackOperationType cashbackOperationType = mapper.toCashbackOperationType(dto);
        CashbackOperationType result = cashbackTariffService.updateOperationType(id, cashbackOperationType);

        return mapper.toCashbackOperationTypeDto(result);
    }

    @DeleteMapping("/{operationTypeId}/operation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOperationCashback(@PathVariable UUID operationTypeId) {
        cashbackTariffService.removeOperationType(operationTypeId);
    }

    @PostMapping("/{tariffId}/tariff/attach-merchant")
    public CashbackMerchantDto addMerchantCashback(@PathVariable UUID tariffId,
                                                   @RequestBody @Valid CreateCashbackMerchantDto dto) {
        CashbackMerchant cashbackMerchant = mapper.toCashbackMerchantEntity(dto);
        CashbackMerchant result = cashbackTariffService.attachMerchantCashback(tariffId, cashbackMerchant);

        return mapper.toCashbackMerchantDto(result);
    }

    @PutMapping("/{id}/merchant")
    public CashbackMerchantDto updateMerchantCashback(@PathVariable UUID id,
                                                      @RequestBody @Valid UpdateCashbackMerchantDto dto) {
        CashbackMerchant cashbackMerchant = mapper.toCashbackMerchantEntity(dto);
        CashbackMerchant result = cashbackTariffService.updateCashbackMerchant(id, cashbackMerchant);

        return mapper.toCashbackMerchantDto(result);
    }

    @DeleteMapping("/{id}/merchant")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMerchantCashback(@PathVariable UUID id) {
        cashbackTariffService.removeMerchantCashback(id);
    }
}
