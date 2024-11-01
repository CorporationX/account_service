package faang.school.accountservice.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

public class SpecsUtils {
    /**
     *
     * @param filters список фильтров
     * @param filterDto дто с параметрами фильтрации
     * @return комбинированная спецификация, включающая в себя все переданные фильтры
     * @param <T> класс дто с параметрами фильтрации ex. AccountFilterDto
     * @param <E> класс сущности, которую фильтруем ex. Account
     */
    public static <T,E> Specification<E> combineSpecsWithAndFromFilters(List<Filter<T,E>> filters, T filterDto) {
        return Specification.allOf(filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.apply(filterDto))
                .collect(Collectors.toSet())
        );
    }
}
