package faang.school.accountservice.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;


@Getter
@Setter
@ToString
@MappedSuperclass
public class BaseEntityWithId {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID uuid;
}
