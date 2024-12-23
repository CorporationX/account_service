package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RequestRepository extends CrudRepository<Request, UUID> {
}