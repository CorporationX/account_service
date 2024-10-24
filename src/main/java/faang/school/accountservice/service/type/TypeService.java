package faang.school.accountservice.service.type;

import faang.school.accountservice.entity.type.AccountType;
import faang.school.accountservice.mapper.type.TypeMapper;
import faang.school.accountservice.repository.type.TypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeService {

    private final TypeRepository typeRepository;
    private final TypeMapper typeMapper;

    public AccountType getTypeByName(String typeName) {
        return typeRepository.findByName(typeName)
                .orElseThrow(() -> new EntityNotFoundException("Type with name " + typeName + " not found"));
    }
}
