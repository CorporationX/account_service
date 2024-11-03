package faang.school.accountservice.filter;

import faang.school.accountservice.dto.filter.FilterDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

public class SpecsUtils {
    /**
     * @param filters   список фильтров
     * @param filterDto дто с параметрами фильтрации
     * @param <T>       класс, реализующий интерфейс FilterDto, с параметрами фильтрации ex. AccountFilterDto
     * @param <E>       класс сущности, которую фильтруем ex. Account
     * @return комбинированная спецификация, включающая в себя все переданные фильтры с условием AND
     */
    public static <T extends FilterDto, E> Specification<E> combineSpecsWithAndConditionFromFilters(List<Filter<T, E>> filters, T filterDto) {
        return Specification.allOf(filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.apply(filterDto))
                .collect(Collectors.toSet())
        );
    }
}
