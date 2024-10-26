package faang.school.accountservice.service.impl.audit;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.audit.BalanceAuditMapper;
import faang.school.accountservice.model.dto.audit.BalanceAuditDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.BalanceAudit;
import faang.school.accountservice.model.enums.OperationType;
import faang.school.accountservice.repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BalanceAuditServiceImplTest {
    @InjectMocks
    private BalanceAuditServiceImpl balanceAuditService;
    @Mock
    private AuditRepository auditRepository;
    @Mock
    private BalanceAuditMapper balanceAuditMapper;

    private Account account;
    private Balance balance;
    private BalanceAudit balanceAudit;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .name("ds")
                .build();
        balance = Balance.builder()
                .account(account)
                .currentAuthorizationBalance(BigDecimal.valueOf(2))
                .currentActualBalance(BigDecimal.valueOf(32))
                .version(1)
                .id(1)
                .build();
        balanceAudit = BalanceAudit.builder()
                .balance(balance)
                .actualBalance(BigDecimal.valueOf(32))
                .authorizationBalance(BigDecimal.valueOf(2))
                .type(OperationType.TRANSLATION)
                .version(1)
                .build();
    }

    @Test
    @DisplayName("Сохранение в БД BalanceAudit")
    void saveBalanceAuditTestOk() {
        //given
        when(auditRepository.save(any(BalanceAudit.class))).thenReturn(balanceAudit);

        //when
        balanceAuditService.saveBalanceAudit(balance, OperationType.TRANSLATION);

        //then
        verify(auditRepository, timeout(1)).save(any(BalanceAudit.class));
    }

    @Test
    @DisplayName("Вернет BalanceAudit, если есть данные в БД по ID")
    void getBalanceAuditTestOk() {
        //given
        BalanceAuditDto dto = BalanceAuditDto.builder()
                .balanceId(1)
                .actualBalance(BigDecimal.valueOf(32))
                .authorizationBalance(BigDecimal.valueOf(2))
                .type(OperationType.TRANSLATION)
                .version(1)
                .build();
        Long id = 1L;

        when(balanceAuditMapper.toDto(any(BalanceAudit.class))).thenReturn(dto);
        when(auditRepository.findById(id)).thenReturn(Optional.of(balanceAudit));

        //when
        var result = balanceAuditService.getBalanceAudit(id);

        //then
        assertEquals(result, dto);
        verify(auditRepository, timeout(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Вернет ошибку, если нет данных в БД по ID")
    void getBalanceAuditTestException() {
        //given
        Long id = 1L;

        when(auditRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(DataValidationException.class, () -> balanceAuditService.getBalanceAudit(id));
        verify(auditRepository, timeout(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Вернет список BalanceAudit, если есть данные в БД")
    void getAllBalanceAuditTestOk() {
        //given
        BalanceAuditDto dto = BalanceAuditDto.builder()
                .balanceId(1)
                .actualBalance(BigDecimal.valueOf(32))
                .authorizationBalance(BigDecimal.valueOf(2))
                .type(OperationType.TRANSLATION)
                .version(1)
                .build();

        List<BalanceAuditDto> dtos = List.of(dto);

        when(balanceAuditMapper.toDto(anyList())).thenReturn(dtos);
        when(auditRepository.findAll()).thenReturn(List.of(balanceAudit));

        //when
        var result = balanceAuditService.getAllBalanceAudit();

        //then
        assertEquals(result, dtos);
        verify(auditRepository, timeout(1)).findAll();
    }

    @Test
    @DisplayName("Вернет пустой список, если не найдено данных в БД")
    void getAllBalanceAuditTestEmpty() {
        //given
        when(auditRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        var result = balanceAuditService.getAllBalanceAudit();

        //then
        assertEquals(result, Collections.emptyList());
        verify(auditRepository, timeout(1)).findAll();
    }
}