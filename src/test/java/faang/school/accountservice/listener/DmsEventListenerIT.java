package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.dms.DmsEventDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.DmsTypeOperation;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Reserve;
import faang.school.accountservice.repository.ReserveRepository;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.jdbc.Sql;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql("/db/account/dms/create_tables.sql")
public class DmsEventListenerIT extends BaseContextTest {
    @Autowired
    public ReserveRepository reserveRepository;

    @Autowired
    private RedisTemplate<String, DmsEventDto>  redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private LocalDateTime now = LocalDateTime.now();
    private DmsEventDto dmsEventDto;
    private Reserve expectedReserve;
    private Balance senderBalance;

    /*
    @BeforeAll
    public void setupBeforeAll() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper = objectMapper;
    }
    */

    @BeforeEach
    public void setup() {
        dmsEventDto = new DmsEventDto(
            3L,
            1L,
            2L,
            BigDecimal.valueOf(100),
            Currency.USD,
            null,
            now
        );
        expectedReserve = Reserve.builder()
            .requestId(dmsEventDto.getRequestId())
            .senderAccount(Account.builder().accountNumber("1111111111111111").build())
            .receiverAccount(Account.builder().accountNumber("2222222222222222").build())
            .amount(dmsEventDto.getAmount())
            .status(DmsTypeOperation.AUTHORIZATION)
            .clearScheduledAt(dmsEventDto.getClearScheduledAt())
            .build();
    }

    @Test
    public void handle_GettingAuthorizationEvent() {
        publishEvent(DmsTypeOperation.AUTHORIZATION);
        Reserve reserve = reserveRepository.findAll().get(0);
        checkReserve(reserve);
    }

    @Test
    public void handle_GettingCancelingEvent() {

    }

    @Test
    public void handle_GettingConfirmationEvent() {

    }

    private void publishEvent(DmsTypeOperation typeOperation) {
        dmsEventDto.setTypeOperation(typeOperation);
        try {
            String message = objectMapper.writeValueAsString(dmsEventDto);
            redisTemplate.convertAndSend("dms_channel", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkReserve(Reserve reserve) {
        assertNotNull(reserve.getId());
        assertEquals(expectedReserve.getRequestId(), reserve.getRequestId());
        assertEquals(expectedReserve.getSenderAccount().getAccountNumber(), reserve.getSenderAccount().getAccountNumber());
        assertEquals(expectedReserve.getReceiverAccount().getAccountNumber(), reserve.getReceiverAccount().getAccountNumber());
        assertEquals(expectedReserve.getAmount(), reserve.getAmount());
        assertEquals(expectedReserve.getStatus(), reserve.getStatus());
        assertEquals(expectedReserve.getClearScheduledAt(), reserve.getClearScheduledAt());
    }
}
