package faang.school.accountservice.handler.dms;

import faang.school.accountservice.dto.dms.DmsEventDto;
import faang.school.accountservice.enums.DmsTypeOperation;

public interface DmsEventHandler {

    void handle(DmsEventDto dmsEventDto);

    DmsTypeOperation getTypeOperation();
}
