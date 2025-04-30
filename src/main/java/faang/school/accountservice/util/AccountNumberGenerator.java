package faang.school.accountservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final JdbcTemplate jdbcTemplate;

    public String generateAccountNumber() {
        Long nextVal = jdbcTemplate.queryForObject("SELECT nextval('account_number_seq')", Long.class);

        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String sequencePart = String.format("%08d", nextVal);

        return datePart + sequencePart;
    }
}
