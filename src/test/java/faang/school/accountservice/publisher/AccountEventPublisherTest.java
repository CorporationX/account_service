package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisProperties;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private RedisProperties redisProperties;
    private AccountEventPublisher accountEventPublisher;

    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        RedisProperties.Channel channel = new RedisProperties.Channel("account_channel");
        redisProperties = new RedisProperties("localhost", 6379, channel);
        accountEventPublisher = new AccountEventPublisher(redisTemplate, redisProperties);

        accountDto = new AccountDto(
                1L,
                "ACC123456",
                AccountOwnerType.USER,
                1001L,
                "Bruce Wayne",
                AccountType.SAVINGS,
                Currency.USD,
                AccountStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("Account event published successfully")
    void testPublish_ValidEvent_Success() {
        accountEventPublisher.publish(accountDto);

        ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        verify(redisTemplate, times(1)).convertAndSend(channelCaptor.capture(), eventCaptor.capture());

        assertThat(channelCaptor.getValue()).isEqualTo("account_channel");
        assertThat(eventCaptor.getValue()).isEqualTo(accountDto);
    }

    @Test
    @DisplayName("Publish null event should not send to Redis")
    void testPublish_NullEvent_NoSend() {
        accountDto = null;

        accountEventPublisher.publish(accountDto);

        verify(redisTemplate, never()).convertAndSend(anyString(), eq(AccountDto.class));
    }
}
