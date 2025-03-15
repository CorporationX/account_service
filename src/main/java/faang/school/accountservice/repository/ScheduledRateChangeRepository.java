package faang.school.accountservice.repository;

import faang.school.accountservice.enums.changerate.Status;
import faang.school.accountservice.model.ScheduledRateChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduledRateChangeRepository extends JpaRepository<ScheduledRateChange, Long> {

    List<ScheduledRateChange> findByScheduledDate(LocalDate date);

    List<ScheduledRateChange> findByScheduledDateAndStatus(LocalDate date, Status status);
}