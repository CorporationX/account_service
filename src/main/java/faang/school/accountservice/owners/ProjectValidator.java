package faang.school.accountservice.owners;

import faang.school.accountservice.enums.OwnerType;
import org.springframework.stereotype.Component;

@Component
public class ProjectValidator implements OwnerValidator {

    //private final ProjectServiceClient projectServiceClient;

    @Override
    public OwnerType getOwnerType() {
        return OwnerType.PROJECT;
    }

    @Override
    public boolean checkOwnerId(Long id) {
        return true;
    }
}
