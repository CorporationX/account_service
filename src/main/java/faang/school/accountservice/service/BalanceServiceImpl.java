package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.dto.project.ProjectDto;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final ProjectServiceClient projectServiceClient;
    private final AccountRepository accountRepository;
    private final UserContext userContext;

    @Override
    public BalanceDto create(CreateBalanceDto createBalanceDto) {
        Account account = accountRepository.findById(createBalanceDto.accountId())
                .orElseThrow(() -> new DataValidationException(
                        "Данного аккаунта не существует, создать его баланс нельзя."));
        checkAccessRightsByAccount(userContext.getUserId(), account);
        Balance balanceToCreate = balanceMapper.toBalance(createBalanceDto);
        balanceToCreate.setAccount(account);
        Balance savedBalance = balanceRepository.save(balanceToCreate);
        log.info("Создан баланс c id: {} аккаунта c id: {}", savedBalance.getId(), account.getId());
        return balanceMapper.toBalanceDto(savedBalance);
    }

    @Override
    public BalanceDto update(long balanceId, UpdateBalanceDto updateBalanceDto) {
        Balance balanceToUpdate = checkAccessRightsByBalanceId(userContext.getUserId(), balanceId);
        balanceMapper.update(updateBalanceDto, balanceToUpdate);
        Balance updatedBalance = balanceRepository.save(balanceToUpdate);
        log.info("Обновлен баланс с id: {}", updatedBalance.getId());
        return balanceMapper.toBalanceDto(updatedBalance);
    }

    @Override
    public BalanceDto getById(long balanceId) {
        Balance balance = checkAccessRightsByBalanceId(userContext.getUserId(), balanceId);
        return balanceMapper.toBalanceDto(balance);
    }

    @Override
    public void delete(long balanceId) {
        checkAccessRightsByBalanceId(userContext.getUserId(), balanceId);
        balanceRepository.deleteById(balanceId);
    }

    private Balance checkAccessRightsByBalanceId(long requesterId, long balanceId) {
        Balance balanceToCheckAccess = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Баланс с таким id не существует."));
        checkAccessRightsByAccount(requesterId, balanceToCheckAccess.getAccount());
        return balanceToCheckAccess;
    }

    private void checkAccessRightsByAccount(long requesterId, Account account) {
        Owner owner = account.getOwner();
        if (owner.getOwnerType() == OwnerType.USER) {
            if (owner.getPersonId() != requesterId) {
                throw new ForbiddenException("Вы не можете получить доступ к балансу чужого аккаунта.");
            }
        } else {
            try {
                ProjectDto projectDto = projectServiceClient.getById(owner.getPersonId());
                if (projectDto.ownerId() != requesterId) {
                    throw new ForbiddenException("Вы не можете получить доступ к балансу чужого аккаунта.");
                }
            } catch (Exception e) {
                if (e instanceof ForbiddenException) {
                    throw e;
                }
                log.warn("Пользователь с id: {} пытается получить доступ к балансу аккаунта, владельцем которого "
                    + "является несуществующий проект с id: {}", requesterId, owner.getPersonId());
                throw new DataValidationException(
                        "Проект-владелец данного аккаунта не существует. Пожалуйста, обратитесь в поддержку.");
            }
        }
    }
}
