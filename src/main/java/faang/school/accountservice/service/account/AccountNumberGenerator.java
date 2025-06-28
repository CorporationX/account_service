package faang.school.accountservice.service.account;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {
    private final EntityManager entityManager;

    @Transactional
    public String generateAccountNumber() {
        Long seqVal = getNextSequenceValue();
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String seqPart = String.format("%08d", seqVal);
        return datePart + seqPart;
    }

    private Long getNextSequenceValue() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('account_number_seq')")
                .getSingleResult())
                .longValue();
    }
}
