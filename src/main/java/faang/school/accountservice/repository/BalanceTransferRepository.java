package faang.school.accountservice.repository;

import faang.school.accountservice.entity.BalanceTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BalanceTransferRepository extends JpaRepository<BalanceTransfer, UUID> {
}