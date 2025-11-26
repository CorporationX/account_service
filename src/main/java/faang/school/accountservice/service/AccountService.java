package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.enums.AccountStatus;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления платежными счетами.
 * Предоставляет функциональность для создания, получения,
 * заморозки и закрытия счетов.
 */
public interface AccountService {


    /**
     * Получить счета конкретного пользователя
     *
     * @param userId ID владельца счета
     * @param projectId ID проекта счета
     * @return список DTO счетов пользователя
     * @throws EntityNotFoundException если проект не существует
     * @throws EntityNotFoundException если пользователь не существует
     */
    List<ResponseAccountDto> getAccounts(Long userId, Long projectId);

    /**
     * Создать новый платежный счет
     *
     * @param createAccountDto DTO с данными для создания счета
     * @return созданный счет в виде DTO
     * @throws EntityNotFoundException если проект не существует
     * @throws EntityNotFoundException если пользователь не существует
     */
    ResponseAccountDto createAccount(CreateAccountDto createAccountDto);

    /**
     * Заблокировать счет
     *
     * @param accountId ID счета
     * @param status статус счета
     * @return обновленный DTO счета
     * @throws EntityNotFoundException если счет не найден
     *  @throws IllegalStateException если переход статуса невозможен
     */
    ResponseAccountDto updateAccountStatus(UUID accountId, AccountStatus status);
}