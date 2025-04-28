package faang.school.accountservice.config.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaProperties {

    // Producer настройки через @Value
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.retries}")
    private Integer producerRetries;

    //Гарантии доставки:
    //all (ждёт подтверждения от всех реплик)
    //1 (только от лидера)
    //0 (никаких гарантий)
    @Value("${spring.kafka.producer.acks}")
    private String producerAcks;

    //Задержка (мс) перед отправкой сообщений для их батчинга (увеличения производительности).
    @Value("${spring.kafka.producer.linger-ms}")
    private Integer producerLingerMs;

    //Максимальный размер (в байтах) батча сообщений перед отправкой.
    @Value("${spring.kafka.producer.batch-size}")
    private Integer producerBatchSize;

    // Consumer настройки через @Value

    //Идентификатор группы потребителей. Позволяет распределять нагрузку между экземплярами приложения.
    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    //Поведение при первом запуске:
    //earliest (читать с начала топика)
    //latest (только новые сообщения)
    //none (ошибка, если offset не сохранён)
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String consumerAutoOffsetReset;

    //Автоматическое подтверждение обработки сообщений:
    //false (ручное управление через Acknowledgment)
    //true (автокоммит с интервалом)
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean consumerEnableAutoCommit;

    //Максимальное количество сообщений, возвращаемых за один вызов poll().
    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer consumerMaxPollRecords;
}
