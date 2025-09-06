package faang.school.accountservice.repository;

import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {

    List<Request> findByIsOpenAndStatus(boolean b, PaymentStages paymentStages);

    List<Request> findByStatus(PaymentStages status);
}