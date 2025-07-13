package faang.school.accountservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventListener<T> {

    public abstract boolean isEventValid(T event);

    public abstract void handle(T event);

    public abstract void handleError(T event);

    public void handleEventWithValidation(T event) {
        if (isEventValid(event)) {
            handle(event);
        } else {
            handleError(event);
        }
    }

    @SafeVarargs
    protected final boolean validateObjectNonNullData(Object o, Supplier<Object>... fieldGetters) {
        return Objects.nonNull(o) && Arrays.stream(fieldGetters).map(Supplier::get).noneMatch(Objects::isNull);
    }
}