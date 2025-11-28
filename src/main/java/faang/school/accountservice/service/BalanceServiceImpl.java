package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
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

    @Override
    public BalanceDto create(long requesterId, CreateBalanceDto createBalanceDto) {
        Balance balanceToCreate = balanceMapper.toBalance(createBalanceDto);
        Account account = accountRepository.findById(createBalanceDto.accountId())
                .orElseThrow(() -> new DataValidationException(
                        "Данного аккаунта не существует, создать его баланс нельзя."));
        balanceToCreate.setAccount(account);
        checkAccessRights(requesterId, balanceToCreate);
        Balance savedBalance = balanceRepository.save(balanceToCreate);
        log.info("Создан баланс c id: {} аккаунта c id: {}", savedBalance.getId(), account.getId());
        return balanceMapper.toBalanceDto(savedBalance);
    }

    @Override
    public BalanceDto update(long requesterId, long balanceId, UpdateBalanceDto updateBalanceDto) {
        Balance balanceToUpdate = balanceMapper.toBalance(updateBalanceDto);
        checkAccessRights(requesterId, balanceToUpdate);
        balanceMapper.update(updateBalanceDto, balanceToUpdate);
        Balance updatedBalance = balanceRepository.save(balanceToUpdate);
        log.info("Обновлен баланс с id: {}", updatedBalance.getId());
        return balanceMapper.toBalanceDto(updatedBalance);
    }

    @Override
    public BalanceDto getById(long requesterId, long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Баланс с таким id не существует."));
        checkAccessRights(requesterId, balance);
        return balanceMapper.toBalanceDto(balance);
    }

    @Override
    public void delete(long requesterId, long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Баланс с таким id не существует."));
        checkAccessRights(requesterId, balance);
        balanceRepository.deleteById(balanceId);
    }

    private void checkAccessRights(long requesterId, Balance balance) {
        Owner owner = balance
                .getAccount()
                .getOwner();
        if (owner.getOwnerType() == OwnerType.USER) {
            if (owner.getPersonId() != requesterId) {
                throw new ForbiddenException("Вы не можете получить доступ к балансу чужого аккаунта.");
            }
        } else {
            ProjectDto projectDto = projectServiceClient.getById(owner.getPersonId());
            if (projectDto == null) {
                log.warn("Пользователь с id: {} пытается получить доступ к балансу аккаунта, владельцем которого "
                        + "является несуществующий проект с id: {}", requesterId, owner.getPersonId());
                throw new DataValidationException(
                        "Проект-владелец данного аккаунта не существует. Пожалуйста, обратитесь в поддержку.");
            }
            if (projectDto.ownerId() != requesterId) {
                throw new ForbiddenException("Вы не можете получить доступ к балансу чужого аккаунта.");
            }
        }
    }
}
