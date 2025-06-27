package faang.school.accountservice.entity.base;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@MappedSuperclass
public class BaseEntityWithVersion extends BaseEntityWithAudit {

    @Version
    private long version;
}
