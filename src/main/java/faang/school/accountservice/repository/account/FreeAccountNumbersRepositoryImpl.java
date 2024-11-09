package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.FreeAccountNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersRepositoryImpl implements FreeAccountNumbersCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllFreeAccountNumbersBatched(List<FreeAccountNumber> freeAccountNumbers) {
        String sql = "INSERT INTO free_account_numbers (type, account_number) VALUES (?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, freeAccountNumbers, freeAccountNumbers.size(),
                    (PreparedStatement ps, FreeAccountNumber accountNumber) -> {
                        ps.setString(1, String.valueOf(accountNumber.getId().getType()));
                        ps.setString(2, accountNumber.getId().getAccountNumber());
                    });
        } catch (DataAccessException dae) {
            log.error("Error occurred while batch save!", dae);
            throw new RuntimeException("Error! " + dae.getMessage() + " ", dae);
        }
    }
}
