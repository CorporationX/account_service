package faang.school.accountservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FreeAccountNumbersRepository {

    private final JdbcTemplate jdbcTemplate;

    public void createFreeNumber(String number, String typeName) {
        log.info("Create new free number: {} for Account Type: {}", number, typeName);
        jdbcTemplate.update("INSERT INTO free_account_numbers (number, type) VALUES (?, ?)", number, typeName);
    }

    public String getFirstFreeNumber(String typeName) {
        log.info("Get first free number for Account Type: {}", typeName);
        try {
            return jdbcTemplate.queryForObject("SELECT number FROM free_account_numbers WHERE type = ? LIMIT 1", String.class, typeName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void deleteByNumber(String number) {
        log.info("Delete number: {} from free numbers", number);
        jdbcTemplate.update("DELETE FROM free_account_numbers WHERE number = ?", number);
    }
}
