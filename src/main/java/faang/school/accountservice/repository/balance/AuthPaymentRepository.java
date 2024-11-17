package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.enums.auth.payment.AuthPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuthPaymentRepository extends JpaRepository<AuthPayment, UUID> {
    @Query("""
                SELECT a FROM AuthPayment a
                WHERE a.sourceBalance.id = :balanceId
                AND a.status = :status
                AND (a.createdAt >= :startPeriod AND a.createdAt < :endPeriod)
            """)
    List<AuthPayment> findBySourceBalanceStatusAndPeriod(AuthPaymentStatus status, UUID balanceId,
                                                         LocalDateTime startPeriod, LocalDateTime endPeriod);
}
