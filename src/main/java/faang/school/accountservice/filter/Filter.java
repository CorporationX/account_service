package faang.school.accountservice.filter;

import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface Filter<T, F> {
    boolean isApplicable(F filter);
    Stream<T> apply(Stream<T> stream, F filter);
}