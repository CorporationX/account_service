package faang.school.accountservice.facade;


import faang.school.accountservice.dto.savingAccount.ResponseSavingDto;
import faang.school.accountservice.dto.savingAccount.SavingCreateDto;
import faang.school.accountservice.entity.SavingAccount;
import faang.school.accountservice.mapper.SavingAccountMapper;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingAccountFacade {

    private final SavingsAccountService savingAccountService;
    private final SavingAccountMapper savingAccountMapper;


    public ResponseSavingDto createdSavingsAccount(SavingCreateDto requestDto) {
        SavingAccount account = savingAccountMapper.toSavingAccount(requestDto);

        SavingAccount createdAccount = savingAccountService.createdSavingsAccount(account);

        ResponseSavingDto responseSavingDto = savingAccountMapper.toResponseSavingDto(createdAccount);

    }


    public void getSavingAccount(UUID savingAccountId) {

    }


    public void updateTariffSavingAccount() {
        // обновить тариф по счету
    }


    public void deleteSavingAccount() {
        // удалить/ удаления счет
    }


    public void depositToAccount() {

    }
    // внести на счет деньги


    public void withdraw() {

    }
    // сниять со счета деньги


}
