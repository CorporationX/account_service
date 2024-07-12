package faang.school.accountservice.service.account_statement;

import faang.school.accountservice.dto.account_statement.AccountStatementDtoToCreate;
import faang.school.accountservice.model.AccountStatement;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.AccountStatementRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.s3.AmazonS3Service;
import faang.school.accountservice.validator.AccountStatementValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountStatementServiceImpl implements AccountStatementService {

    private final AccountStatementRepository accountStatementRepository;
    private final AccountStatementValidator accountStatementValidator;
    private final AmazonS3Service amazonS3Service;
    private final BalanceAuditRepository balanceAuditRepository;

    @Override
    public InputStream createAccountStatementPDFfile(AccountStatementDtoToCreate dto, long userId) {
        List<BalanceAudit> balanceAudits = balanceAuditRepository.findAllAuditsByInterval(dto.getAccountId(), dto.getFrom(), dto.getTo());

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText("Statement of Account");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Date");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Charges");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Credits");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Account Balance");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                int yPosition = 680;
                for (BalanceAudit audit : balanceAudits) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText(audit.getCreatedAt().toString());
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(audit.getAuthorizationBalance().toString());
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(audit.getActualBalance().toString());
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(String.valueOf(audit.getVersion()));
                    contentStream.endText();

                    yPosition -= 20;
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            document.close();

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to create PDF", e);
        }
    }

    @Override
    public InputStream getFileFromS3(String key, long userId) {
        accountStatementValidator.validateGetFile(key, userId);

        InputStream fileInputStream = amazonS3Service.downloadFile(key);

        log.info("File with key {} retrieved successfully for user {}", key, userId);

        return fileInputStream;
    }
    @Override
    public void uploadPdfFileToS3(MultipartFile file, long userId) {
        accountStatementValidator.validateUploadFile(file, userId);

        String key = amazonS3Service.uploadFile(file);
        AccountStatement accountStatement = AccountStatement.builder()
                .key(key)
                .size(file.getSize())
                .build();
        accountStatementRepository.save(accountStatement);

        log.info("Successfully uploaded file: {}", file.getOriginalFilename());
    }

    @Override
    public InputStream downloadFileFromS3(String key, long userId) {
        accountStatementValidator.validateDownloadFile(key, userId);

        return amazonS3Service.downloadFile(key);
    }

    @Override
    @Transactional
    public void deleteFileFromS3(String key, Long userId) {
        accountStatementValidator.validateDeleteFile(key, userId);

        accountStatementValidator.validateExistenceByKey(key);

        accountStatementRepository.deleteByKey(key);
        amazonS3Service.deleteFile(key);

        log.info("Successfully deleted file: {}", key);
    }
}
