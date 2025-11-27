package faang.school.accountservice.service.operation;

import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.OperationType;

public interface OperationHandler {

    /**
     * Возвращает тип операции, который обрабатывает этот handler
     */
    OperationType getSupportedOperationType();

    /**
     * Выполняет бизнес-логику операции
     */
    void execute(Request request);
}