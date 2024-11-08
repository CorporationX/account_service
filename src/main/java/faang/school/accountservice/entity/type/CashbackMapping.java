package faang.school.accountservice.entity.type;

public interface CashbackMapping {
    // этот интерфейс нужен, что бы сделать в AbstractCashback вот эту фичу: ? extends CashbackMapping
    // что бы не закинуть неправильный дженерик
}
