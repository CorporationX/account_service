package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountUpdateDto;
import faang.school.accountservice.dto.account.AccountViewDto;

/**
 * Интерфейс, предоставляющий операции по открытию, получения платежного счета, а так же
 * его заморозки или блокировки
 *
 * @author mrnght
 * @since 22.08.2025
 */
public interface AccountService {

    /**
     * Открытие счета
     * @param createDto параметры создаваемого счета
     * @return {@link AccountViewDto} открытый счет
     */
    AccountViewDto openAccount(AccountCreateDto createDto);

    /**
     * Получение платежного счета
     * @param id идентификатор счета
     * @return {@link AccountViewDto} платежный счет
     */
    AccountViewDto getAccount(Long id);

    /**
     * Закрытие счета
     * @param id идентификатор счета
     * @return {@link AccountViewDto} закрытый счет
     */
    AccountViewDto changeAccountStatus(Long id, AccountUpdateDto updateDto);
}
