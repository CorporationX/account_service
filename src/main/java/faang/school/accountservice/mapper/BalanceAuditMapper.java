package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.audit.ResponseAuditDto;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BalanceAuditMapper {

   public ResponseAuditDto toDto(BalanceAudit audit) {
       User user = audit.getInitiator();

       return ResponseAuditDto.builder()
               .balanceAuditId(audit.getId())
               .userId(user.getId())
               .username(user.getUsername())
               .email(user.getEmail())
               .auditEventType(audit.getEventType())
               .currentAuthAmount(audit.getCurrentAuthAmount())
               .previousAuthAmount(audit.getPreviousAuthAmount())
               .currentFactAmount(audit.getCurrentFactAmount())
               .previousFactAmount(audit.getPreviousFactAmount())
               .auditedAt(audit.getAuditedAt())
               .build();
   }
}