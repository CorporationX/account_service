package faang.school.accountservice.mapper.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.request.*;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.exception.SerializationException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Map;

import static faang.school.accountservice.messages.ErrorMessages.FAILED_TO_CONVERT_JSON_TO_MAP;
import static faang.school.accountservice.messages.ErrorMessages.FAILED_TO_CONVERT_MAP_TO_JSON;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {
    @Mapping(source = "type", target = "type")
    @Mapping(source = "inputData", target = "inputData")
    Request requestCreationDtoToRequest(RequestCreationDto dto);

    @Mapping(source = "type", target = "type")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "inputData", target = "inputData")
    RequestResponseDto requestToRequestResponseDto(Request entity);

    @Mapping(source = "idempotencyToken", target = "idempotencyToken")
    @Mapping(source = "status", target = "status")
    RequestStatusResponseDto requestToRequestStatusResponseDto(Request request);

    default String mapInputDataToJson(Map<String, Object> inputData) {
        if (inputData == null) {
            return null;
        }
        try {
            return new ObjectMapper().writeValueAsString(inputData);
        } catch (JsonProcessingException e) {
            throw new SerializationException(FAILED_TO_CONVERT_MAP_TO_JSON);
        }
    }

    default Map<String, Object> mapJsonToInputData(String inputData) {
        if (inputData == null || inputData.isEmpty()) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(inputData, Map.class);
        } catch (JsonProcessingException e) {
            throw new SerializationException(FAILED_TO_CONVERT_JSON_TO_MAP);
        }
    }

    default RequestStatusDto requestStatusToRequestStatusDto(RequestStatus status) {
        return status == null ? null : RequestStatusDto.valueOf(status.name());
    }

    default RequestTypeDto requestTypeToRequestTypeDto(RequestType type) {
        return type == null ? null : RequestTypeDto.valueOf(type.name());
    }

    default RequestType requestTypeDtoToRequestType(RequestTypeDto typeDto) {
        return typeDto == null ? null : RequestType.valueOf(typeDto.name());
    }
}