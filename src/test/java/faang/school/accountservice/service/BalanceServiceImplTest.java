//package faang.school.accountservice.service;
//
//import faang.school.accountservice.dto.BalanceDto;
//import faang.school.accountservice.entity.Balance;
//import faang.school.accountservice.mapper.BalanceAuditMapper;
//import faang.school.accountservice.mapper.BalanceMapper;
//import faang.school.accountservice.repository.BalanceAuditRepository;
//import faang.school.accountservice.repository.BalanceJpaRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//@ExtendWith(MockitoExtension.class)
//class BalanceServiceImplTest {
//    @InjectMocks
//    private BalanceServiceImpl service;
//
//    @Mock
//    private BalanceAuditRepository balanceAuditRepository;
//    @Mock
//    private BalanceJpaRepository balanceJpaRepository;
//    @Mock
//    private BalanceAuditMapper auditMapper;
//    @Mock
//    private BalanceMapper mapper;
//
//    private BalanceDto balanceDto;
//    private Balance balance;
//    private final long accountId = 1L;
//
//    @BeforeEach
//    public void init() {
//        balance = new Balance();
//        balance.setId(1L);
//
//        balance.setVersion(1);
//
//        balanceDto = new BalanceDto();
//        balanceDto.setId(1L);
//        balanceDto.setAccountId(1L);
//
//        Mockito.lenient().when(mapper.toDto(balance))
//                .thenReturn(balanceDto);
//        Mockito.lenient().when(mapper.toEntity(balanceDto))
//                .thenReturn(balance);
//        Mockito.lenient().when(balanceJpaRepository.findById(accountId))
//                .thenReturn(Optional.of(balance));
//    }
//
//    @Test
//    void create_whenOk() {
//        service.create(balanceDto);
//
//        Mockito.verify(balanceJpaRepository, Mockito.times(1))
//                .save(balance);
//        Mockito.verify(mapper, Mockito.times(1))
//                .toEntity(balanceDto);
//        Mockito.verify(balanceAuditRepository, Mockito.times(1))
//                .save(auditMapper.toEntity(balance));
//    }
//
//    @Test
//    void update_Balance_whenOk() {
//        final ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);
//
//        service.update(balanceDto);
//
//        Mockito.verify(balanceJpaRepository, Mockito.times(1))
//                .save(captor.capture());
//        Mockito.verify(balanceAuditRepository, Mockito.times(1))
//                .save(auditMapper.toEntity(balance));
//
//        Balance actual = captor.getValue();
//        Assertions.assertNotNull(actual.getUpdatedAt());
//        Assertions.assertNull(actual.getCreatedAt());
//    }
//
//    @Test
//    void getBalance_whenOk() {
//        service.getBalance(accountId);
//
//        Mockito.verify(mapper, Mockito.times(1))
//                .toDto(balance);
//        Mockito.verify(balanceJpaRepository, Mockito.times(1))
//                .findById(accountId);
//
//    }
//
//    @Test
//    void getBalance_whenAccountNotExist() {
//        Mockito.lenient().when(balanceJpaRepository.findById(accountId))
//                .thenReturn(Optional.ofNullable(null));
//
//        Assertions.assertThrows(EntityNotFoundException.class, () -> service.getBalance(accountId));
//
//        Mockito.verify(mapper, Mockito.never())
//                .toDto(balance);
//
//    }
//}
