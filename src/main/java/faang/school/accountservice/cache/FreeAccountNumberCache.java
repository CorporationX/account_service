package faang.school.accountservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FreeAccountNumberCache {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public FreeAccountNumber getFreeAccount(AccountType type, Currency currency) {
        String partialKey = "free_account_number: " + type.name() + "_" + currency.name();
        Set<String> keys = redisTemplate.keys(partialKey + "*");

        if (keys != null && !keys.isEmpty()) {
            String firstKey = keys.iterator().next();
            String json = (String) redisTemplate.opsForValue().get(firstKey);
            try {
                FreeAccountNumber cachedAccount = objectMapper.readValue(json, FreeAccountNumber.class);
                redisTemplate.delete(firstKey);

                return cachedAccount;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return freeAccountNumbersRepository.retrieveFirst(type.name(), currency.name())
                    .orElseThrow(() -> new IllegalArgumentException("No more free accounts in database table 'free_account_numbers'"));
        }
    }

    public void updateRedisCache(List<FreeAccountNumber> numbers) {
        try {
            for (FreeAccountNumber number : numbers) {
                String value = objectMapper.writeValueAsString(number);
                String key = "free_account_number: " + number.getId().getType().name() + "_" + number.getId().getCurrency().name() + "_" + number.getId().getAccountNumber();
                redisTemplate.opsForValue().set(key, value);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error put list free account numbers in Redis cache");
        }
    }
}
