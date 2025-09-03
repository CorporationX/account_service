package faang.school.accountservice;

import faang.school.accountservice.utils.AccountNumberGenerator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AppRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Set<String> accounts = AccountNumberGenerator.generateAccountNumber(10);
        System.out.println(accounts);
    }
}
