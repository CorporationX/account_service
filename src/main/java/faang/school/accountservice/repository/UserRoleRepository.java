package faang.school.accountservice.repository;

import faang.school.accountservice.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    boolean existsByUserIdAndRole(Long userId, String role);
}