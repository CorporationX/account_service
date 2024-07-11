package faang.school.accountservice.dto.account_statement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class dgf {
    private Long id;
    private String key;
    private long size;
    private LocalDateTime createdAt;
    private String name;
    private String type;
}