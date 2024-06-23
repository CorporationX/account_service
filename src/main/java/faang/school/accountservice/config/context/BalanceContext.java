package faang.school.accountservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class BalanceContext {
    private final ThreadLocal<Long> balanceIdHolder = new ThreadLocal<>();

    public void setBalanceId(long id){
        balanceIdHolder.set(id);
    }
    public long getBalanceId(){
        return balanceIdHolder.get();
    }
    public void clear(){
        balanceIdHolder.remove();
    }

}
