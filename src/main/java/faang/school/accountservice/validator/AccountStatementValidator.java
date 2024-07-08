package faang.school.accountservice.validator;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.property.AmazonS3Properties;
import faang.school.accountservice.repository.AccountStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountStatementValidator {

    private final AccountStatementRepository accountStatementRepository;
    private final AmazonS3Properties amazonS3Properties;

    @Transactional
    public void validateCountFilesPerPost(Long postId, int filesToAdd) {
        if (accountStatementRepository.countAllByPost_Id(postId) + filesToAdd > amazonS3Properties.getMaxFilesAmount()) {
            throw new DataValidationException(String.format("Max files per post = %s", amazonS3Properties.getMaxFilesAmount()));
        }
    }

    @Transactional
    public void validateExistenceByKey(String key) {
        if (!accountStatementRepository.existsByKey(key)) {
            throw new NotFoundException(String.format("Resource with key $s not found", key));
        }
    }

    public void validatePostAuthorAndResourceAuthor(Long postAuthorId, Long postProjectId, Long resourceUserId) {
        if (!postAuthorId.equals(resourceUserId) && !postProjectId.equals(resourceUserId)) {
            throw new NotFoundException("Mismatch postAuthorIdId and resourceUserId");
        }
    }
}