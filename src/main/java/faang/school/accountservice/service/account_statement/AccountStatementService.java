package faang.school.accountservice.service.account_statement;

import faang.school.accountservice.dto.account_statement.AccountStatementDtoToCreate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface AccountStatementService {

    InputStream getHistory(AccountStatementDtoToCreate accountId, long userId);

    InputStream getFile(String key, long userId);

    void createFileAndUpload(MultipartFile file, long userId);

    void deleteFile(String key, Long userId);

    InputStream downloadFile(String key, long userId);
}