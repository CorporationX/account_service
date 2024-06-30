package faang.school.accountservice.service.payment;

import faang.school.accountservice.client.PaymentServiceClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    private final PaymentServiceClient paymentServiceClient;


    /**
     * @TODO
     * 0. Сохранение истории (и если сохраняем, как мы генерируем id
     * и будут ли у нас одинаковые идентификаторы одного и того же платежа, если платеж мы отменяем)
     * 1. Проверка баланса платежа в случае если баланс меньше суммы, направляем отмену заявки обратно в payment_service
     * 2. Если с балансом все ок, "замораживаем баланс" до клиринга. После клиригна списываем деньги
     * 2.1 Если в процессе приходит запрос на отмену платежа, отменяем платеж, размораживаем деньги
     *
     * в п.2.1 подумать, что делать в ситуации, когда пользователь отменяет платеж в последнюю секунду до клиринга
     * и клиринг запущен
     */
}
