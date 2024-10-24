package faang.school.accountservice.mapper.owner;

import faang.school.accountservice.dto.owner.OwnerDto;
import faang.school.accountservice.entity.owner.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OwnerMapper {

    OwnerDto toOwnerDto(Owner owner);

    Owner toEntity(OwnerDto ownerDto);
}
