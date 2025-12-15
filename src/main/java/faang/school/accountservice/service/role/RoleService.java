package faang.school.accountservice.service.role;

import faang.school.accountservice.enums.Role;

public interface RoleService {

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @param userId идентификатор пользователя
     * @return true, если пользователь имеет роль ADMIN, иначе false
     */
    boolean isAdmin(Long userId);

    /**
     * Проверяет, имеет ли пользователь указанную роль.
     *
     * @param userId идентификатор пользователя
     * @param role   роль для проверки
     * @return true, если пользователь имеет указанную роль, иначе false
     */
    boolean hasRole(Long userId, Role role);
}