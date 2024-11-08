package faang.school.accountservice.integration.service.balance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.dto.payment.response.AuthPaymentResponse;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.dto.payment.response.ClearingPaymentResponse;
import faang.school.accountservice.dto.payment.response.ErrorPaymentResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.payment.Currency;
import faang.school.accountservice.enums.payment.Category;
import faang.school.accountservice.integration.config.RedisPostgresTestContainers;
import faang.school.accountservice.integration.config.listener.redis.listeners.AuthPaymentResponseTestRedisListener;
import faang.school.accountservice.integration.config.listener.redis.listeners.CancelPaymentResponseTestRedisListener;
import faang.school.accountservice.integration.config.listener.redis.listeners.ClearingPaymentResponseTestRedisListener;
import faang.school.accountservice.integration.config.listener.redis.listeners.ErrorPaymentResponseTestRedisListener;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static faang.school.accountservice.enums.payment.Currency.USD;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.ACTIVE;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.REJECTED;
import static faang.school.accountservice.enums.payment.AccountBalanceStatus.BALANCE_NOT_VERIFIED;
import static faang.school.accountservice.enums.payment.AccountBalanceStatus.INSUFFICIENT_FUNDS;
import static faang.school.accountservice.enums.payment.AccountBalanceStatus.SUFFICIENT_FUNDS;
import static faang.school.accountservice.enums.payment.Category.OTHER;
import static faang.school.accountservice.enums.payment.PaymentStatus.FAILED;
import static faang.school.accountservice.enums.payment.PaymentStatus.SUCCESS;
import static faang.school.accountservice.util.fabrics.AccountFabric.buildAccountDefault;
import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildAuthPaymentRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = "/test-sql/insert-default-accounts-and-balances.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/test-sql/truncate-balance-account.sql", executionPhase = AFTER_TEST_METHOD)
@ActiveProfiles("testLiquibaseRedis")
@SpringBootTest
public class BalanceServiceIntegrationTest extends RedisPostgresTestContainers {
    private static final UUID SOURCE_ACCOUNT_ID = UUID.fromString("065977b1-2f8d-47d5-a2a7-c88671a3c5a3");
    private static final UUID TARGET_ACCOUNT_ID = UUID.fromString("f6309d7b-22bd-4b18-a4fa-29a6bdd502e8");
    private static final UUID SOURCE_BALANCE_ID = UUID.fromString("4cc8cd27-9c53-4e4c-8f44-de6a6d7182c0");
    private static final UUID TARGET_BALANCE_ID = UUID.fromString("bd4a870b-8ffa-4919-a1a4-57c0cb1138a3");
    private static final BigDecimal SOURCE_BALANCE_CURRENT_BALANCE = BigDecimal.valueOf(1000);
    private static final BigDecimal SOURCE_BALANCE_AUTH_BALANCE = BigDecimal.valueOf(0);
    private static final BigDecimal TARGET_BALANCE_CURRENT_BALANCE = BigDecimal.valueOf(0);
    private static final BigDecimal TARGET_BALANCE_AUTH_BALANCE = BigDecimal.valueOf(0);
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(20);
    private static final BigDecimal AMOUNT_MORE_THAN_CURRENT_BALANCE = BigDecimal.valueOf(2000);
    private static final Currency CURRENCY = USD;
    private static final Category CATEGORY = OTHER;

    @Value("${spring.data.redis.channel.auth-payment.request}")
    private String authPaymentRequestTopicName;

    @Value("${spring.data.redis.channel.clearing-payment.request}")
    private String clearingPaymentRequestTopicName;

    @Value("${spring.data.redis.channel.cancel-payment.request}")
    private String cancelPaymentRequestTopicName;

    @Value("${spring.data.redis.channel.error-payment.request}")
    String errorPaymentRequestTopicName;

    @Autowired
    private AuthPaymentResponseTestRedisListener authPaymentResponseListener;

    @Autowired
    private ClearingPaymentResponseTestRedisListener clearingPaymentResponseListener;

    @Autowired
    private CancelPaymentResponseTestRedisListener cancelPaymentResponseListener;

    @Autowired
    private ErrorPaymentResponseTestRedisListener errorPaymentResponseListener;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthPaymentRepository authPaymentRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    void testCreateBalance_successful() {
        Account account = accountRepository.save(buildAccountDefault(5L));
        Balance balance = balanceService.createBalance(account);
        assertThat(balanceRepository.findById(balance.getId()).orElseThrow()).isNotNull();
    }

    @Test
    void testAuthorizePayment_successful() throws JsonProcessingException {
        AuthPaymentRequest authPaymentRequest = buildAuthPaymentRequest(UUID.randomUUID(), SOURCE_ACCOUNT_ID,
                TARGET_ACCOUNT_ID, AMOUNT, CURRENCY, CATEGORY);
        String authPaymentRequestJson = objectMapper.writeValueAsString(authPaymentRequest);
        redisTemplate.convertAndSend(authPaymentRequestTopicName, authPaymentRequestJson);

        sleep(200);

        AuthPaymentResponse expectedAuthPaymentResponse = AuthPaymentResponse.builder()
                .operationId(authPaymentRequest.getOperationId())
                .status(SUFFICIENT_FUNDS)
                .paymentStatus(SUCCESS)
                .build();
        String authPaymentResponseJson = authPaymentResponseListener.getReceivedMessage();
        AuthPaymentResponse authPaymentResponse = objectMapper.readValue(authPaymentResponseJson, AuthPaymentResponse.class);

        assertThat(authPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthPaymentResponse);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE.subtract(AMOUNT));
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE.add(AMOUNT));

        AuthPayment authPayment = authPaymentRepository.findById(authPaymentRequest.getOperationId()).orElseThrow();
        assertThat(authPayment.getSourceBalance().getId()).isEqualTo(SOURCE_BALANCE_ID);
        assertThat(authPayment.getTargetBalance().getId()).isEqualTo(TARGET_BALANCE_ID);
        assertThat(authPayment.getAmount()).isEqualTo(AMOUNT);
        assertThat(authPayment.getStatus()).isEqualTo(ACTIVE);
        assertThat(authPayment.getCategory()).isEqualTo(CATEGORY);
    }

    @Test
    void testAuthorizePayment_validationException() throws JsonProcessingException {
        AuthPaymentRequest authPaymentRequest = buildAuthPaymentRequest(UUID.randomUUID(), SOURCE_ACCOUNT_ID,
                TARGET_ACCOUNT_ID, AMOUNT_MORE_THAN_CURRENT_BALANCE, CURRENCY, CATEGORY);
        String authPaymentRequestJson = objectMapper.writeValueAsString(authPaymentRequest);
        redisTemplate.convertAndSend(authPaymentRequestTopicName, authPaymentRequestJson);

        sleep(200);

        AuthPaymentResponse expectedAuthPaymentResponse = AuthPaymentResponse.builder()
                .operationId(authPaymentRequest.getOperationId())
                .status(INSUFFICIENT_FUNDS)
                .paymentStatus(FAILED)
                .build();
        String authPaymentResponseJson = authPaymentResponseListener.getReceivedMessage();
        AuthPaymentResponse authPaymentResponse =
                objectMapper.readValue(authPaymentResponseJson, AuthPaymentResponse.class);

        assertThat(authPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthPaymentResponse);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);

        Optional<AuthPayment> authPayment = authPaymentRepository.findById(authPaymentRequest.getOperationId());
        assertThat(authPayment).isEmpty();
    }

    @Test
    void testAuthPayment_optimisticLockException() throws JsonProcessingException, InterruptedException {
        Thread parallelUpdateThread = new Thread(() -> IntStream.rangeClosed(0, 100)
                .forEach(i -> {
                    Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
                    sourceBalance.setCurrentBalance(sourceBalance.getCurrentBalance().add(BigDecimal.valueOf(1)));
                    balanceRepository.save(sourceBalance);
                }));
        parallelUpdateThread.start();

        AuthPaymentRequest authPaymentRequest = buildAuthPaymentRequest(UUID.randomUUID(), SOURCE_ACCOUNT_ID,
                TARGET_ACCOUNT_ID, AMOUNT, CURRENCY, CATEGORY);
        String authPaymentRequestJson = objectMapper.writeValueAsString(authPaymentRequest);
        redisTemplate.convertAndSend(authPaymentRequestTopicName, authPaymentRequestJson);

        sleep(200);

        AuthPaymentResponse expectedAuthPaymentResponse = AuthPaymentResponse.builder()
                .operationId(authPaymentRequest.getOperationId())
                .status(BALANCE_NOT_VERIFIED)
                .paymentStatus(FAILED)
                .build();
        String authPaymentResponseJson = authPaymentResponseListener.getReceivedMessage();
        AuthPaymentResponse authPaymentResponse =
                objectMapper.readValue(authPaymentResponseJson, AuthPaymentResponse.class);

        assertThat(authPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthPaymentResponse);

        Optional<AuthPayment> authPayment = authPaymentRepository.findById(authPaymentRequest.getOperationId());
        assertThat(authPayment).isEmpty();

        parallelUpdateThread.join();
    }

    @Test
    void testClearingPayment_successful() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        sourceBalance.setCurrentBalance(sourceBalance.getCurrentBalance().subtract(AMOUNT));
        sourceBalance.setAuthBalance(sourceBalance.getAuthBalance().add(AMOUNT));
        balanceRepository.save(sourceBalance);

        UUID operationId = UUID.randomUUID();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(ACTIVE)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        ClearingPaymentRequest clearingPaymentRequest = new ClearingPaymentRequest(operationId);
        String clearingPaymentRequestJson = objectMapper.writeValueAsString(clearingPaymentRequest);
        redisTemplate.convertAndSend(clearingPaymentRequestTopicName, clearingPaymentRequestJson);

        sleep(200);

        ClearingPaymentResponse expectedClearingPaymentResponse = ClearingPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(SUCCESS)
                .build();
        String clearingPaymentResponseJson = clearingPaymentResponseListener.getReceivedMessage();
        ClearingPaymentResponse clearingPaymentResponse =
                objectMapper.readValue(clearingPaymentResponseJson, ClearingPaymentResponse.class);

        assertThat(clearingPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedClearingPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE.subtract(AMOUNT));

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE.add(AMOUNT));

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    void testClearingPayment_validationException_statusClosed() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        UUID operationId = UUID.randomUUID();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(CLOSED)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        ClearingPaymentRequest clearingPaymentRequest = new ClearingPaymentRequest(operationId);
        String clearingPaymentRequestJson = objectMapper.writeValueAsString(clearingPaymentRequest);
        redisTemplate.convertAndSend(clearingPaymentRequestTopicName, clearingPaymentRequestJson);

        sleep(200);

        ClearingPaymentResponse expectedClearingPaymentResponse = ClearingPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(FAILED)
                .build();
        String clearingPaymentResponseJson = clearingPaymentResponseListener.getReceivedMessage();
        ClearingPaymentResponse clearingPaymentResponse =
                objectMapper.readValue(clearingPaymentResponseJson, ClearingPaymentResponse.class);

        assertThat(clearingPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedClearingPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE);

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    void testClearingPayment_validationException_statusRejected() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        UUID operationId = UUID.randomUUID();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(REJECTED)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        ClearingPaymentRequest clearingPaymentRequest = new ClearingPaymentRequest(operationId);
        String clearingPaymentRequestJson = objectMapper.writeValueAsString(clearingPaymentRequest);
        redisTemplate.convertAndSend(clearingPaymentRequestTopicName, clearingPaymentRequestJson);

        sleep(200);

        ClearingPaymentResponse expectedClearingPaymentResponse = ClearingPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(FAILED)
                .build();
        String clearingPaymentResponseJson = clearingPaymentResponseListener.getReceivedMessage();
        ClearingPaymentResponse clearingPaymentResponse =
                objectMapper.readValue(clearingPaymentResponseJson, ClearingPaymentResponse.class);

        assertThat(clearingPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedClearingPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE);

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    void testCancelPayment_successful() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        sourceBalance.setCurrentBalance(sourceBalance.getCurrentBalance().subtract(AMOUNT));
        sourceBalance.setAuthBalance(sourceBalance.getAuthBalance().add(AMOUNT));
        balanceRepository.save(sourceBalance);

        UUID operationId = UUID.randomUUID();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(ACTIVE)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        CancelPaymentRequest cancelPaymentRequest = new CancelPaymentRequest(operationId);
        String cancelPaymentRequestJson = objectMapper.writeValueAsString(cancelPaymentRequest);
        redisTemplate.convertAndSend(cancelPaymentRequestTopicName, cancelPaymentRequestJson);

        sleep(200);

        CancelPaymentResponse expectedCancelPaymentResponse = CancelPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(SUCCESS)
                .build();
        String cancelPaymentResponseJson = cancelPaymentResponseListener.getReceivedMessage();
        CancelPaymentResponse cancelPaymentResponse =
                objectMapper.readValue(cancelPaymentResponseJson, CancelPaymentResponse.class);

        assertThat(cancelPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedCancelPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE);

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    void testCancelPayment_validationException_statusClosed() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        UUID operationId = UUID.randomUUID();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(CLOSED)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        CancelPaymentRequest cancelPaymentRequest = new CancelPaymentRequest(operationId);
        String cancelPaymentRequestJson = objectMapper.writeValueAsString(cancelPaymentRequest);
        redisTemplate.convertAndSend(cancelPaymentRequestTopicName, cancelPaymentRequestJson);

        sleep(200);

        CancelPaymentResponse expectedCancelPaymentResponse = CancelPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(FAILED)
                .build();
        String cancelPaymentResponseJson = cancelPaymentResponseListener.getReceivedMessage();
        CancelPaymentResponse cancelPaymentResponse =
                objectMapper.readValue(cancelPaymentResponseJson, CancelPaymentResponse.class);

        assertThat(cancelPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedCancelPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE);

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    void testCancelPayment_validationException_statusRejected() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        UUID operationId = UUID.randomUUID();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(REJECTED)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        CancelPaymentRequest cancelPaymentRequest = new CancelPaymentRequest(operationId);
        String cancelPaymentRequestJson = objectMapper.writeValueAsString(cancelPaymentRequest);
        redisTemplate.convertAndSend(cancelPaymentRequestTopicName, cancelPaymentRequestJson);

        sleep(200);

        CancelPaymentResponse expectedCancelPaymentResponse = CancelPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(FAILED)
                .build();
        String cancelPaymentResponseJson = cancelPaymentResponseListener.getReceivedMessage();
        CancelPaymentResponse cancelPaymentResponse =
                objectMapper.readValue(cancelPaymentResponseJson, CancelPaymentResponse.class);

        assertThat(cancelPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedCancelPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE);

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    void testErrorPayment_successful() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        sourceBalance.setCurrentBalance(sourceBalance.getCurrentBalance().subtract(AMOUNT));
        sourceBalance.setAuthBalance(sourceBalance.getAuthBalance().add(AMOUNT));
        balanceRepository.save(sourceBalance);

        UUID operationId = UUID.randomUUID();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(ACTIVE)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        ErrorPaymentRequest errorPaymentRequest = new ErrorPaymentRequest(operationId);
        String errorPaymentRequestJson = objectMapper.writeValueAsString(errorPaymentRequest);
        redisTemplate.convertAndSend(errorPaymentRequestTopicName, errorPaymentRequestJson);

        sleep(200);

        ErrorPaymentResponse expectedErrorPaymentResponse = ErrorPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(SUCCESS)
                .build();
        String errorPaymentResponseJson = errorPaymentResponseListener.getReceivedMessage();
        ErrorPaymentResponse errorPaymentResponse =
                objectMapper.readValue(errorPaymentResponseJson, ErrorPaymentResponse.class);

        assertThat(errorPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedErrorPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE);

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    void testErrorPayment_validationException_statusClosed() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        UUID operationId = UUID.randomUUID();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(CLOSED)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        ErrorPaymentRequest errorPaymentRequest = new ErrorPaymentRequest(operationId);
        String errorPaymentRequestJson = objectMapper.writeValueAsString(errorPaymentRequest);
        redisTemplate.convertAndSend(errorPaymentRequestTopicName, errorPaymentRequestJson);

        sleep(200);

        ErrorPaymentResponse expectedErrorPaymentResponse = ErrorPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(FAILED)
                .build();
        String errorPaymentResponseJson = errorPaymentResponseListener.getReceivedMessage();
        ErrorPaymentResponse errorPaymentResponse =
                objectMapper.readValue(errorPaymentResponseJson, ErrorPaymentResponse.class);

        assertThat(errorPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedErrorPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE);

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    void testErrorPayment_validationException_statusRejected() throws JsonProcessingException {
        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        UUID operationId = UUID.randomUUID();
        AuthPayment authPayment = AuthPayment.builder()
                .id(operationId)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(AMOUNT)
                .status(REJECTED)
                .category(CATEGORY)
                .build();
        authPaymentRepository.save(authPayment);

        ErrorPaymentRequest errorPaymentRequest = new ErrorPaymentRequest(operationId);
        String errorPaymentRequestJson = objectMapper.writeValueAsString(errorPaymentRequest);
        redisTemplate.convertAndSend(errorPaymentRequestTopicName, errorPaymentRequestJson);

        sleep(200);

        ErrorPaymentResponse expectedErrorPaymentResponse = ErrorPaymentResponse.builder()
                .operationId(operationId)
                .paymentStatus(FAILED)
                .build();
        String errorPaymentResponseJson = errorPaymentResponseListener.getReceivedMessage();
        ErrorPaymentResponse errorPaymentResponse =
                objectMapper.readValue(errorPaymentResponseJson, ErrorPaymentResponse.class);

        assertThat(errorPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedErrorPaymentResponse);

        sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthBalance()).isEqualTo(SOURCE_BALANCE_AUTH_BALANCE);
        assertThat(sourceBalance.getCurrentBalance()).isEqualTo(SOURCE_BALANCE_CURRENT_BALANCE);

        targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthBalance()).isEqualTo(TARGET_BALANCE_AUTH_BALANCE);
        assertThat(targetBalance.getCurrentBalance()).isEqualTo(TARGET_BALANCE_CURRENT_BALANCE);

        authPayment = authPaymentRepository.findById(operationId).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(REJECTED);
    }

    @SuppressWarnings("SameParameterValue")
    void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
