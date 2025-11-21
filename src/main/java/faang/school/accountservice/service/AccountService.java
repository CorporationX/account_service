package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import org.springframework.data.domain.Pageable;

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
     * @return список DTO счетов пользователя
     */
    List<ResponseAccountDto> getAccountsByUserId(Long userId, Pageable pageable);

    /**
     * Получить счета конкретного проекта
     *
     * @param projectId ID проекта счета
     * @return список DTO счетов проекта
     */
    List<ResponseAccountDto> getAccountsByProjectId(Long projectId, Pageable pageable);

    /**
     * Создать новый платежный счет
     *
     * @param CreateAccountDto DTO с данными для создания счета
     * @return созданный счет в виде DTO
     * @throws IllegalArgumentException если проект не существует
     * @throws IllegalArgumentException если пользователь не существует
     */
    ResponseAccountDto createAccount(CreateAccountDto createAccountDto);

    /**
     * Заблокировать счет
     *
     * @param accountId ID счета
     * @return обновленный DTO счета
     * @throws IllegalArgumentException если счет не найден или не принадлежит владельцу
     */
    ResponseAccountDto blockAccount(UUID accountId);

    /**
     * Закрыть счет
     *
     * @param accountId ID счета
     * @return обновленный DTO счета
     * @throws IllegalArgumentException если счет не найден или не принадлежит владельцу
     */
    ResponseAccountDto closeAccount(UUID accountId);
}