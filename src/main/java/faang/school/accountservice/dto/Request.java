package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.mapper.JsonConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;
import java.util.Map;

public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private RequestType requestType;
    private Long lockValue;
    @NotNull
    private boolean isOpenRequest;
    @Column(columnDefinition = "input_data")
    @Convert(converter = JsonConverter.class)
    private Map<Object, String> inputData;
    @NotNull
    private RequestStatus requestStatus;
    private String additionalData;
    private ZonedDateTime createdAt;
    private  ZonedDateTime updatedAt;
    private int version;

}
