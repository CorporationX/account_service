package faang.school.accountservice.service.account_statement;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import faang.school.accountservice.mapper.AccountStatementMapper;
import faang.school.accountservice.repository.AccountStatementRepository;
import faang.school.accountservice.service.s3.AmazonS3Service;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountStatementServiceImpl implements AccountStatementService {

    private final AccountStatementRepository accountStatementRepository;
    private final AccountStatementMapper accountStatementMapper;
    private final ResourceValidator resourceValidator;
    private final AmazonS3Service amazonS3Service;
    private final PostService postService;

    @Override
    @Transactional
    public Resource findById(Long id) {
        return accountStatementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Resource with id %s not found", id)));
    }

    @Override
    @Transactional
    public List<ResourceDto> create(Long postId, Long userId, List<MultipartFile> files) {

        Post post = postService.findById(postId);
        resourceValidator.validatePostAuthorAndResourceAuthor(post.getAuthorId(), post.getProjectId(), userId);
        resourceValidator.validateCountFilesPerPost(postId, files.size());

        try(ExecutorService executorService = Executors.newFixedThreadPool(files.size())) {
            List<CompletableFuture<Resource>> resources = new ArrayList<>();
            List<Resource> savedResources = new ArrayList<>();

            files.forEach(file -> {
                CompletableFuture<Resource> resource = CompletableFuture.supplyAsync(() -> {
                    String key = amazonS3Service.uploadFile(file);
                    return Resource.builder()
                            .name(file.getOriginalFilename())
                            .key(key)
                            .size(file.getSize())
                            .type(file.getContentType())
                            .post(post)
                            .build();
                }, executorService);
                resources.add(resource);
            });

            resources.forEach(resource -> {
                Resource resourceToSave = resource.join();
                savedResources.add(accountStatementRepository.save(resourceToSave));
            });

            log.info("Successfully create resource");
            return savedResources.stream()
                    .map(accountStatementMapper::toDto)
                    .toList();

        } catch (AmazonS3Exception ex) {
            log.error(ex.getMessage());
            throw new S3Exception(ex.getMessage());
        }
    }

    @Override
    public InputStream downloadResource(String key) {
        resourceValidator.validateExistenceByKey(key);
        return amazonS3Service.downloadFile(key);
    }

    @Override
    @Transactional
    public void deleteFile(String key, Long userId) {
        Resource resourceToRemove = accountStatementRepository.findByKey(key);
        Post post = resourceToRemove.getPost();

        resourceValidator.validatePostAuthorAndResourceAuthor(post.getAuthorId(), post.getProjectId(), userId);
        resourceValidator.validateExistenceByKey(key);

        accountStatementRepository.deleteByKey(key);
        amazonS3Service.deleteFile(key);

        log.error("Successfully delete file from resources");
    }
}