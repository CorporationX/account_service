package faang.school.accountservice.service.account_statement;

import faang.school.accountservice.dto.account_statement.AccountStatementDto;
import faang.school.accountservice.model.AccountStatement;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface AccountStatementService {

    AccountStatement findById(Long id);

    List<AccountStatementDto> create(Long postId, Long userId, List<MultipartFile> files);

    InputStream downloadResource(String key);

    void deleteFile(String key, Long userId);
}