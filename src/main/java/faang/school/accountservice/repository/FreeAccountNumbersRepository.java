package faang.school.accountservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FreeAccountNumbersRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Long> getFreeAccountNumbers(int quantity, String sequenceName) {
        String query = "select nextval(:sequence) as free_number from generate_series(1, :quantity)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("quantity", quantity);
        params.addValue("sequence", sequenceName);

        return namedParameterJdbcTemplate.queryForList(query, params, Long.class);
    }
}