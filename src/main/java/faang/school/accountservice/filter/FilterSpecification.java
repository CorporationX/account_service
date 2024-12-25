package faang.school.accountservice.filter;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public interface FilterSpecification<T, F> {
    boolean isApplicable(F filter);
    Specification<T> apply( F filter);
}
