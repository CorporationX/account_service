package faang.school.accountservice.filter;

import faang.school.accountservice.dto.filter.FilterDto;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @param <T> класс, реализующий интерфейс FilterDto, с параметрами фильтрации ex. AccountFilterDto
 * @param <E> класс сущности, которую фильтруем ex. Account
 */
public interface Filter<T extends FilterDto, E> {

    boolean isApplicable(T filters);

    Specification<E> apply(T filters);
}
