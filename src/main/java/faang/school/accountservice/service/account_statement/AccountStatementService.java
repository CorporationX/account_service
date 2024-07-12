package faang.school.accountservice.service.account_statement;

import faang.school.accountservice.dto.account_statement.AccountStatementDtoToCreate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface AccountStatementService {

    InputStream createAccountStatementPDFfile(AccountStatementDtoToCreate accountId, long userId);

    InputStream getFileFromS3(String key, long userId);

    void uploadPdfFileToS3(MultipartFile file, long userId);

    void deleteFileFromS3(String key, Long userId);

    InputStream downloadFileFromS3(String key, long userId);
}