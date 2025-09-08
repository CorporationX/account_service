package faang.school.accountservice.dto;

import lombok.Builder;

@Builder
public record AccountDto(Long id, Long number, String accountType, String accountStatus, String currency ) {
}
