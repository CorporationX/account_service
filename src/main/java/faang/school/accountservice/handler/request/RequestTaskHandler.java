package faang.school.accountservice.handler.request;

import faang.school.accountservice.enums.RequestHandlerType;

public interface RequestTaskHandler<T> {
    void execute(T param);

    RequestHandlerType getHandlerId();
}
