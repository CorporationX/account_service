package faang.school.accountservice.filter;

import faang.school.accountservice.model.Account;

import java.util.stream.Stream;

public interface Filter<T, E> {

    boolean isApplicable(T filters);

    Stream<E> apply(Stream<E> stream, T filters);
}
