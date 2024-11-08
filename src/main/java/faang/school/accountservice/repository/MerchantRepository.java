package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    @Query("SELECT m FROM Merchant m WHERE m.userId = :userId")
    Merchant findByUserId(long userId);

    @Query("SELECT m FROM Merchant m WHERE m.projectId = :projectId")
    Merchant findByProjectId(long projectId);
}