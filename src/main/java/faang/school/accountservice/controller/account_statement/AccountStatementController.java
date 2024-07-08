package faang.school.accountservice.controller.account_statement;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.resource.ResourceDto;
import faang.school.accountservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("resources")
@RequiredArgsConstructor
@Tag(name = "Resource")
public class AccountStatementController {

    private final ResourceService resourceService;
    private final UserContext userContext;

    @Operation(summary = "Save resource")
    @PostMapping("upload/{postId}")
    public List<ResourceDto> uploadFiles(
            @PathVariable Long postId,
            @RequestPart List<MultipartFile> files
    ) {
        return resourceService.create(postId, userContext.getUserId(), files);
    }

    @Operation(summary = "Download resource")
    @GetMapping("download/{key}")
    public InputStream downloadFile(@PathVariable String key) {
        return resourceService.downloadResource(key);
    }

    @Operation(summary = "Delete resource")
    @DeleteMapping("{key}")
    public void deleteFile(@PathVariable String key) {
        resourceService.deleteFile(key, userContext.getUserId());
    }
}