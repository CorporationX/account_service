package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountAlreadyClosedException;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.balance.BalanceService2;
import faang.school.accountservice.validation.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Сервис для работы с банковскими счетами.
 * Содержит бизнес-логику для операций со счетами.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;
    private final AccountHelper accountHelper;
    private final BalanceService2 balanceService2;

    /**
     * Получает информацию о счете по его идентификатору.
     *
     * @param accountId идентификатор счета
     * @return данные счета в формате {@link AccountViewDto}
     * @throws AccountNotFoundException если счет не найден
     */
    @Transactional(readOnly = true)
    public AccountViewDto getAccount(Long accountId) {
        Account account = getAccountById(accountId);
        return accountMapper.toViewDto(account);
    }

    /**
     * Получает список счетов для указанного владельца с поддержкой пагинации.
     *
     * @param ownerType тип владельца счета
     * @param ownerId   идентификатор владельца
     * @param pageable  параметры пагинации
     * @return пагинированный список счетов в формате {@link Page<AccountViewDto>}
     */
    @Transactional(readOnly = true)
    public Page<AccountViewDto> getAccountsByOwner(OwnerType ownerType, Long ownerId, Pageable pageable) {
        Page<Account> accounts = accountRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId, pageable);
        return accounts.map(accountMapper::toViewDto);
    }

    /**
     * Создает новый счет на основе предоставленных данных.
     *
     * @param createDto данные для создания счета {@link AccountCreateDto}
     * @return данные созданного счета в формате {@link AccountViewDto}
     */
    @Transactional
    public AccountViewDto openAccount(AccountCreateDto createDto) {
        String accountNumber = accountHelper.generateUniqueAccountNumber();
        Account account = accountMapper.toEntity(createDto);
        account.setAccountNumber(accountNumber);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setVersion(0);
        account = accountHelper.saveAccount(account);
        balanceService2.createBalanceForAccount(account.getId());
        return accountMapper.toViewDto(account);
    }

    /**
     * Блокирует счет по его идентификатору.
     *
     * @param accountId идентификатор счета
     * @return данные заблокированного счета в формате {@link AccountViewDto}
     * @throws AccountNotFoundException          если счет не найден
     * @throws AccountOperationConflictException если счет уже заблокирован
     */
    @Transactional
    public AccountViewDto blockAccount(Long accountId) {
        Account account = getAccountById(accountId);
        accountValidator.validateBlock(account);
        return updateAccountStatus(account, AccountStatus.BLOCKED, null);
    }

    /**
     * Закрывает счет по его идентификатору.
     *
     * @param accountId идентификатор счета
     * @return данные закрытого счета в формате {@link AccountViewDto}
     * @throws AccountNotFoundException          если счет не найден
     * @throws AccountAlreadyClosedException     если счет уже закрыт
     * @throws AccountOperationConflictException если баланс счета не равен нулю
     */
    @Transactional
    public AccountViewDto closeAccount(Long accountId) {
        Account account = getAccountById(accountId);
        accountValidator.validateClose(account);
        return updateAccountStatus(account, AccountStatus.CLOSED, Instant.now());
    }

    /**
     * Разблокирует счет по его идентификатору.
     *
     * @param accountId идентификатор счета
     * @return данные разблокированного счета в формате {@link AccountViewDto}
     * @throws AccountNotFoundException          если счет не найден
     * @throws AccountOperationConflictException если счет не заблокирован
     */
    @Transactional
    public AccountViewDto unblockAccount(Long accountId) {
        Account account = getAccountById(accountId);
        accountValidator.validateUnblock(account);
        return updateAccountStatus(account, AccountStatus.ACTIVE, null);
    }

    /**
     * Получает учетную запись по идентификатору.
     *
     * @param accountId идентификатор учетной записи
     * @return учетная запись
     * @throws AccountNotFoundException если учетная запись не найдена
     */
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", accountId);
                    return new AccountNotFoundException(String.format("Account not found with id: %d", accountId));
                });
    }

    /**
     * Обновляет статус счета и, при необходимости, время закрытия.
     *
     * @param account   счет для обновления
     * @param newStatus новый статус счета
     * @param closedAt  время закрытия (может быть null)
     * @return данные обновленного счета в формате {@link AccountViewDto}
     */
    private AccountViewDto updateAccountStatus(Account account, AccountStatus newStatus, Instant closedAt) {
        accountValidator.validateStatus(account, newStatus);
        account.setAccountStatus(newStatus);
        account.setClosedAt(closedAt);
        account = accountHelper.saveAccount(account);
        return accountMapper.toViewDto(account);
    }
}