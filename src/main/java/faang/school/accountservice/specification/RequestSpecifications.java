package faang.school.accountservice.specification;

import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.OperationType;
import faang.school.accountservice.enums.request.RequestStatus;
import org.springframework.data.jpa.domain.Specification;

public class RequestSpecifications {

    public static Specification<Request> hasUserId(Long userId) {
        return (root, query, cb) -> userId != null
                ? cb.equal(root.get("userId"), userId) : null;
    }

    public static Specification<Request> hasProjectId(Long projectId) {
        return (root, query, cb) -> projectId != null
                ? cb.equal(root.get("projectId"), projectId) : null;
    }

    public static Specification<Request> hasOperationType(OperationType operationType) {
        return (root, query, cb)
                -> operationType != null ? cb.equal(root.get("operationType"), operationType) : null;
    }

    public static Specification<Request> hasLockValue(String lockValue) {
        return (root, query, cb)
                -> lockValue != null ? cb.equal(root.get("lockValue"), lockValue) : null;
    }

    public static Specification<Request> isOpen() {
        return (root, query, cb) -> cb.isTrue(root.get("isOpen"));
    }

    public static Specification<Request> hasStatus(RequestStatus status) {
        return (root, query, cb) -> cb.equal(root.get("requestStatus"),
                status);
    }

    public static Specification<Request> findOpenByLock(String lockValue) {
        return hasLockValue(lockValue).and(isOpen());
    }
}
