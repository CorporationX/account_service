package faang.school.accountservice.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

public class SpecsUtils {
    public static <T,E> Specification<E> combineSpecsFromFilters(List<Filter<T,E>> filters, T filterDto) {
        return Specification.allOf(filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.apply(filterDto))
                .collect(Collectors.toSet())
        );
    }
}
