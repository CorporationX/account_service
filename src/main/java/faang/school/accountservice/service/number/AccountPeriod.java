package faang.school.accountservice.service.number;

/*
    интервал доступных номеров аккаунтов, возвращается при успешном инкременте номеров
    fromInclusive — первое новое значение счётчика,
    toInclusive — последнее новое значение счётчика.
 */
public record AccountPeriod(long fromInclusive, long toInclusive) {
}