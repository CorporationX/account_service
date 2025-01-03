package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings.SavingsAccountResponseDto;
import faang.school.accountservice.model.savings.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

  @Mapping(target = "tariff", source = "tariffHistory", ignore = true)
  SavingsAccountResponseDto toDto(SavingsAccount account);
  //TODO 3.01: настроить чтобы один послелний тариф все-таки добавлял, но при этом
  // не поламать, если уже где-то использовался

//  @Mapping(target = "tariff", source = "tariffHistory", qualifiedByName = "currentTariff")
//  SavingsAccountResponseDto toDto(SavingsAccount account);

  //TODO переделать, чтобы TariffDto возвращал? так не получиться скорее всего - в
  // может быть сделать отдельный маппинг? один как
  // все что ниже не используется
//  @Named("currentTariff")
//  default String map(String tariffHistory) {
//    String [] str = tariffHistory.split(",");
//    int index = str.length - 1;
//    return str[index].trim().replaceAll("[\\[%\\]]", "");
    //TODO не знаяю получится ли просто маппингом, скорее всего нет
    // может получить все запросом как-то? подумать как лучше
    // или как идея, в какой-то Map/Json сразу записывать или прохоить по дто ...
//  }

}
