package faang.school.accountservice.validator.account;

import org.springframework.stereotype.Component;

@Component
public class AccountServiceValidator {
    public void checkId(long...id){
        for (int i = 0; i <id.length ; i++) {
            if(id[i]<0){
                throw new IllegalArgumentException("Id must be > 0");
            }
        }
    }
}
