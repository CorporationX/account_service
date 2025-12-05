package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.enums.request.RequestStatus;

import java.util.UUID;

/**
 * Сервис для хранения информации о запросах или операциях в системе.
 * Предоставляет функциональность для управления жизненным циклом запросов,
 * обеспечивает идемпотентность операций и механизм блокировок.
 */
public interface RequestService {

    /**
     * Создать новый запрос
     *
     * @param idempotencyToken токен идемпотентности
     * @param createRequestDto DTO с данными для хранения запроса
     * @return созданный или существующий запрос в виде DTO
     * @throws EntityNotFoundException если проект или пользователь не существует
     * @throws IllegalArgumentException если токен уже используется с другими данными
     * @throws IllegalStateException если существует открытый запрос с таким же lockValue
     */
    ResponseRequestDto createRequest(UUID idempotencyToken, CreateRequestDto createRequestDto);

    /**
     * Обновить запрос (статус, флаг открытия, детали статуса)
     * Флаг открытия (isOpen) обновляется автоматически на основе статуса:
     *      * для финальных статусов (COMPLETED, FAILED, CANCELLED) — isOpen = false, для остальных — true.
     *
     * @param idempotencyToken токен идемпотентности запроса
     * @param statusDetails дополнительные детали статуса
     * @return обновленный DTO запроса
     * @throws EntityNotFoundException если запрос не найден
     * @throws IllegalStateException если переход статуса невозможен
     */
    ResponseRequestDto updateRequestStatus(UUID idempotencyToken, RequestStatus status, String statusDetails);
}
