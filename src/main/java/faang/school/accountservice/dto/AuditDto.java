package faang.school.accountservice.dto;

import lombok.Data;

@Data
public class AuditDto {
    private Long id;
    private String number;
    private Long version;
}
