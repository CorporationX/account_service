package faang.school.accountservice.service.role;

import faang.school.accountservice.enums.Role;
import faang.school.accountservice.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRoleRepository userRoleRepository;

    @Override
    public boolean isAdmin(Long userId) {
        return hasRole(userId, Role.ADMIN);
    }

    @Override
    public boolean hasRole(Long userId, Role role) {
        if (userId == null || role == null) {
            return false;
        }
        boolean hasRole = userRoleRepository.existsByUserIdAndRole(userId, role.name());
        log.debug("Checking role {} for user {}: {}", role, userId, hasRole);
        return hasRole;
    }
}