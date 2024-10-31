package faang.school.accountservice.filter;

import org.springframework.data.jpa.domain.Specification;


public interface Filter<T, E> {

    boolean isApplicable(T filters);

    Specification<E> apply(T filters);
}
