package faang.school.accountservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AccountNumbersSequenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public void createCounterForType(String typeName) {
        log.info("Create counter for Account Type: {}", typeName);
        jdbcTemplate.update("INSERT INTO account_numbers_sequence (counter, type) VALUES (0, ?)", typeName);
    }

    public Long getCurrentCounter(String typeName) {
        log.info("Get current counter for {}", typeName);
        try {
            return jdbcTemplate.queryForObject("SELECT counter FROM account_numbers_sequence WHERE type = ?", Long.class, typeName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long incrementCounter(Long counter, String typeName) {
        log.info("Increment counter: {} for Account Type: {}", counter, typeName);
        return jdbcTemplate.queryForObject("UPDATE account_numbers_sequence SET counter = ? WHERE type = ? and counter = ? RETURNING counter",
                Long.class, counter + 1, typeName, counter);
    }
}
