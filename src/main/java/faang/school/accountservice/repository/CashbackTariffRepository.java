package faang.school.accountservice.repository;

import faang.school.accountservice.entity.cacheback.CashbackTariff;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashbackTariffRepository extends JpaRepository<CashbackTariff, UUID> {
    @EntityGraph(attributePaths = {"cashbackOperationTypes", "cashbackMerchants"})
    @Query("SELECT ct FROM CashbackTariff ct WHERE ct.id = :id")
    Optional<CashbackTariff> findByIdWithRelations(UUID id);
}