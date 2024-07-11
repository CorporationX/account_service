package faang.school.accountservice.service.account_statement;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import faang.school.accountservice.dto.account_statement.AccountStatementDtoToCreate;
import faang.school.accountservice.mapper.AccountStatementMapper;
import faang.school.accountservice.model.AccountStatement;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.AccountStatementRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.balance_audit.BalanceAuditService;
import faang.school.accountservice.service.s3.AmazonS3Service;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.exception.S3Exception;
import faang.school.accountservice.validator.AccountStatementValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountStatementServiceImpl implements AccountStatementService {

    private final AccountStatementRepository accountStatementRepository;
    private final AccountStatementMapper accountStatementMapper;
    private final AccountStatementValidator accountStatementValidator;
    private final AmazonS3Service amazonS3Service;
    private final BalanceAuditRepository balanceAuditRepository;

    @Override
    public MultipartFile getHistory(AccountStatementDtoToCreate dto, long userId) {
        List<BalanceAudit> balanceAudits = balanceAuditRepository.findAllAuditsByInterval(dto.getAccountId(), dto.getFrom(), dto.getTo());

        throw new UnsupportedOperationException("Method not implemented yet.");
    }

    @Override
    public MultipartFile getFile(String key, long userId) {
        accountStatementValidator.validateExistenceByKey(key);
        MultipartFile file = amazonS3Service.downloadFile(key);
        return file; // This method should just send user on page where the PDF file is opened, not download on the computer
    }

    @Override
    public void createFileAndUpload(MultipartFile file, long userId) {
        accountStatementValidator.validateCreateFileAndUpload(file, userId);

        String key = amazonS3Service.uploadFile(file);
        AccountStatement accountStatement = AccountStatement.builder()
                .key(key)
                .size(file.getSize())
                .build();
        accountStatementRepository.save(accountStatement);

        log.info("Successfully uploaded file: {}", file.getOriginalFilename());
    }

    @Override
    public InputStream downloadFile(String key, long userId) {
        accountStatementValidator.validateExistenceByKey(key);
        return amazonS3Service.downloadFile(key);
    }

    @Override
    @Transactional
    public void deleteFile(String key, Long userId) {
        AccountStatement accountStatementToRemove = accountStatementRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("Account statement not found for key: " + key));
        accountStatementValidator.validateExistenceByKey(key);

        accountStatementRepository.deleteByKey(key);
        amazonS3Service.deleteFile(key);

        log.info("Successfully deleted file: {}", key);
    }
}