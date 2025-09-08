package faang.school.accountservice.dto;

public record CreateAccountDto(Long creatorId, String ownerType, Long ownerId,
                               String accountType, String currency) {
}
