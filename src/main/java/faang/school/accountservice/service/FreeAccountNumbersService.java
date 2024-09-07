package faang.school.accountservice.service;

import org.springframework.stereotype.Service;

@Service
public class FreeAccountNumbersService {
    // метод получения и удаления свободного номера счёта
    public String getFreeAccountNumber() {
        return "Свободный номер счёта.";
    }

}
