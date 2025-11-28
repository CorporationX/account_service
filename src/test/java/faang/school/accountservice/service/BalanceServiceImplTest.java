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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {
    @InjectMocks
    private BalanceServiceImpl balanceServiceImpl;

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private AccountRepository accountRepository;

    private long anyLong;
    private long anyDifferentLong;
    private long anyRequesterId;
    private Balance anyBalance;
    private Account anyAccount;
    private Owner anyOwner;
    private CreateBalanceDto anyCreateBalanceDto;
    private UpdateBalanceDto anyUpdateBalanceDto;
    private BalanceDto anyBalanceDto;
    private ProjectDto anyProjectDto;


    @BeforeEach
    public void setUp() {
        anyLong = 1L;
        anyDifferentLong = 2L;
        anyRequesterId = anyLong;
        anyBalance = new Balance();
        anyAccount = new Account();
        anyOwner = new Owner();
        anyBalance.setAccount(anyAccount);
        anyAccount.setOwner(anyOwner);
        anyCreateBalanceDto = new CreateBalanceDto(anyLong);
        anyUpdateBalanceDto = new UpdateBalanceDto(anyLong, anyLong, anyLong);
        anyBalanceDto = new BalanceDto(anyLong, anyLong, anyLong, anyLong);
    }

    @Test
    public void createFromNonexistentAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyLong);

        assertThrows(DataValidationException.class, () -> balanceServiceImpl.create(anyLong, anyCreateBalanceDto));
        verify(balanceMapper, times(1)).toBalance(any(CreateBalanceDto.class));
    }

    @Test
    public void createFromAlienUserAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyDifferentLong);

        when(accountRepository.findById(anyCreateBalanceDto.accountId())).thenReturn(Optional.of(anyAccount));

        assertThrows(ForbiddenException.class, () -> balanceServiceImpl.create(anyLong, anyCreateBalanceDto));
        verify(balanceMapper, times(1)).toBalance(any(CreateBalanceDto.class));
        verify(accountRepository, times(1)).findById(anyCreateBalanceDto.accountId());
    }

    @Test
    public void createFromNonexistentProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyDifferentLong);

        when(accountRepository.findById(anyCreateBalanceDto.accountId())).thenReturn(Optional.of(anyAccount));

        assertThrows(DataValidationException.class, () -> balanceServiceImpl.create(anyLong, anyCreateBalanceDto));
        verify(balanceMapper, times(1)).toBalance(any(CreateBalanceDto.class));
        verify(accountRepository, times(1)).findById(anyCreateBalanceDto.accountId());
    }

    @Test
    public void createFromAlienProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyDifferentLong);
        anyProjectDto = new ProjectDto(anyLong, anyDifferentLong);

        when(accountRepository.findById(anyCreateBalanceDto.accountId())).thenReturn(Optional.of(anyAccount));
        when(projectServiceClient.getById(anyOwner.getPersonId())).thenReturn(anyProjectDto);

        assertThrows(ForbiddenException.class, () -> balanceServiceImpl.create(anyLong, anyCreateBalanceDto));
        verify(balanceMapper, times(1)).toBalance(any(CreateBalanceDto.class));
        verify(accountRepository, times(1)).findById(anyCreateBalanceDto.accountId());
        verify(projectServiceClient, times(1)).getById(anyOwner.getPersonId());
    }

    @Test
    public void createSuccessfullyCreatesFromUserAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyLong);

        when(balanceMapper.toBalance(any(CreateBalanceDto.class))).thenReturn(anyBalance);
        when(accountRepository.findById(anyCreateBalanceDto.accountId())).thenReturn(Optional.of(anyAccount));
        when(balanceRepository.save(anyBalance)).thenReturn(anyBalance);

        balanceServiceImpl.create(anyRequesterId, anyCreateBalanceDto);

        verify(balanceMapper, times(1)).toBalance(any(CreateBalanceDto.class));
        verify(accountRepository, times(1)).findById(anyCreateBalanceDto.accountId());
        verify(balanceRepository, times(1)).save(anyBalance);
        verify(balanceMapper, times(1)).toBalanceDto(anyBalance);
    }

    @Test
    public void createSuccessfullyCreatesFromProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyLong);
        anyProjectDto = new ProjectDto(anyLong, anyLong);

        when(balanceMapper.toBalance(any(CreateBalanceDto.class))).thenReturn(anyBalance);
        when(accountRepository.findById(anyCreateBalanceDto.accountId())).thenReturn(Optional.of(anyAccount));
        when(projectServiceClient.getById(anyOwner.getPersonId())).thenReturn(anyProjectDto);
        when(balanceRepository.save(anyBalance)).thenReturn(anyBalance);

        balanceServiceImpl.create(anyRequesterId, anyCreateBalanceDto);

        verify(balanceMapper, times(1)).toBalance(any(CreateBalanceDto.class));
        verify(accountRepository, times(1)).findById(anyCreateBalanceDto.accountId());
        verify(projectServiceClient, times(1)).getById(anyOwner.getPersonId());
        verify(balanceRepository, times(1)).save(anyBalance);
        verify(balanceMapper, times(1)).toBalanceDto(anyBalance);
    }

    @Test
    public void updateFromAlienUserAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyDifferentLong);

        when(balanceMapper.toBalance(any(UpdateBalanceDto.class))).thenReturn(anyBalance);

        assertThrows(ForbiddenException.class, () -> balanceServiceImpl.update(anyLong, anyLong, anyUpdateBalanceDto));
        verify(balanceMapper, times(1)).toBalance(any(UpdateBalanceDto.class));
    }

    @Test
    public void updateFromNonexistentProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyDifferentLong);

        when(balanceMapper.toBalance(any(UpdateBalanceDto.class))).thenReturn(anyBalance);

        assertThrows(DataValidationException.class, () -> balanceServiceImpl.update(anyLong, anyLong, anyUpdateBalanceDto));
        verify(balanceMapper, times(1)).toBalance(any(UpdateBalanceDto.class));
    }

    @Test
    public void updateFromAlienProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyDifferentLong);
        anyProjectDto = new ProjectDto(anyLong, anyDifferentLong);

        when(balanceMapper.toBalance(any(UpdateBalanceDto.class))).thenReturn(anyBalance);
        when(projectServiceClient.getById(anyOwner.getPersonId())).thenReturn(anyProjectDto);

        assertThrows(ForbiddenException.class, () -> balanceServiceImpl.update(anyLong, anyLong, anyUpdateBalanceDto));
        verify(balanceMapper, times(1)).toBalance(any(UpdateBalanceDto.class));
        verify(projectServiceClient, times(1)).getById(anyOwner.getPersonId());
    }

    @Test
    public void updateSuccessfullyUpdatesFromUserAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyLong);

        when(balanceMapper.toBalance(any(UpdateBalanceDto.class))).thenReturn(anyBalance);
        when(balanceRepository.save(anyBalance)).thenReturn(anyBalance);

        balanceServiceImpl.update(anyRequesterId, anyLong, anyUpdateBalanceDto);

        verify(balanceMapper, times(1)).toBalance(any(UpdateBalanceDto.class));
        verify(balanceRepository, times(1)).save(anyBalance);
        verify(balanceMapper, times(1)).toBalanceDto(anyBalance);
    }

    @Test
    public void updateSuccessfullyUpdatesFromProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyLong);
        anyProjectDto = new ProjectDto(anyLong, anyLong);

        when(balanceMapper.toBalance(any(UpdateBalanceDto.class))).thenReturn(anyBalance);
        when(projectServiceClient.getById(anyOwner.getPersonId())).thenReturn(anyProjectDto);
        when(balanceRepository.save(anyBalance)).thenReturn(anyBalance);

        balanceServiceImpl.update(anyRequesterId, anyLong, anyUpdateBalanceDto);

        verify(balanceMapper, times(1)).toBalance(any(UpdateBalanceDto.class));
        verify(projectServiceClient, times(1)).getById(anyOwner.getPersonId());
        verify(balanceRepository, times(1)).save(anyBalance);
        verify(balanceMapper, times(1)).toBalanceDto(anyBalance);
    }

    @Test
    public void getByIdNonexistentBalance() {
        assertThrows(EntityNotFoundException.class, () -> balanceServiceImpl.getById(anyLong, anyLong));

        verify(balanceRepository, times(1)).findById(anyLong);
    }

    @Test
    public void getByIdFromAlienUserAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyDifferentLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));

        assertThrows(ForbiddenException.class, () -> balanceServiceImpl.getById(anyLong, anyLong));
        verify(balanceRepository, times(1)).findById(anyLong);
    }

    @Test
    public void getByIdFromNonexistentProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyDifferentLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));

        assertThrows(DataValidationException.class, () -> balanceServiceImpl.getById(anyLong, anyLong));
        verify(balanceRepository, times(1)).findById(anyLong);
    }

    @Test
    public void getByIdFromAlienProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyDifferentLong);
        anyProjectDto = new ProjectDto(anyLong, anyDifferentLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));
        when(projectServiceClient.getById(anyOwner.getPersonId())).thenReturn(anyProjectDto);

        assertThrows(ForbiddenException.class, () -> balanceServiceImpl.getById(anyLong, anyLong));
        verify(balanceRepository, times(1)).findById(anyLong);
        verify(projectServiceClient, times(1)).getById(anyOwner.getPersonId());
    }

    @Test
    public void getByIdSuccessfullyCreatesFromUserAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));

        balanceServiceImpl.getById(anyLong, anyLong);

        verify(balanceRepository, times(1)).findById(anyLong);
        verify(balanceMapper, times(1)).toBalanceDto(anyBalance);
    }

    @Test
    public void getByIdSuccessfullyCreatesFromProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyLong);
        anyProjectDto = new ProjectDto(anyLong, anyLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));
        when(projectServiceClient.getById(anyOwner.getPersonId())).thenReturn(anyProjectDto);

        balanceServiceImpl.getById(anyLong, anyLong);

        verify(projectServiceClient, times(1)).getById(anyOwner.getPersonId());
        verify(balanceRepository, times(1)).findById(anyLong);
        verify(balanceMapper, times(1)).toBalanceDto(anyBalance);
    }

    @Test
    public void deleteNonexistentBalance() {
        assertThrows(EntityNotFoundException.class, () -> balanceServiceImpl.delete(anyLong, anyLong));

        verify(balanceRepository, times(1)).findById(anyLong);
    }

    @Test
    public void deleteFromAlienUserAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyDifferentLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));

        assertThrows(ForbiddenException.class, () -> balanceServiceImpl.delete(anyLong, anyLong));
        verify(balanceRepository, times(1)).findById(anyLong);
    }

    @Test
    public void deleteFromNonexistentProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyDifferentLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));

        assertThrows(DataValidationException.class, () -> balanceServiceImpl.delete(anyLong, anyLong));
        verify(balanceRepository, times(1)).findById(anyLong);
    }

    @Test
    public void deleteFromAlienProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyDifferentLong);
        anyProjectDto = new ProjectDto(anyLong, anyDifferentLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));
        when(projectServiceClient.getById(anyOwner.getPersonId())).thenReturn(anyProjectDto);

        assertThrows(ForbiddenException.class, () -> balanceServiceImpl.delete(anyLong, anyLong));
        verify(balanceRepository, times(1)).findById(anyLong);
        verify(projectServiceClient, times(1)).getById(anyOwner.getPersonId());
    }

    @Test
    public void deleteSuccessfullyCreatesFromUserAccount() {
        anyOwner.setOwnerType(OwnerType.USER);
        anyOwner.setPersonId(anyLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));

        balanceServiceImpl.delete(anyLong, anyLong);

        verify(balanceRepository, times(1)).findById(anyLong);
        verify(balanceRepository, times(1)).deleteById(anyLong);
    }

    @Test
    public void deleteSuccessfullyCreatesFromProjectAccount() {
        anyOwner.setOwnerType(OwnerType.PROJECT);
        anyOwner.setPersonId(anyLong);
        anyProjectDto = new ProjectDto(anyLong, anyLong);

        when(balanceRepository.findById(anyLong)).thenReturn(Optional.of(anyBalance));
        when(projectServiceClient.getById(anyOwner.getPersonId())).thenReturn(anyProjectDto);

        balanceServiceImpl.delete(anyLong, anyLong);

        verify(balanceRepository, times(1)).findById(anyLong);
        verify(projectServiceClient, times(1)).getById(anyOwner.getPersonId());
        verify(balanceRepository, times(1)).deleteById(anyLong);
    }
}
