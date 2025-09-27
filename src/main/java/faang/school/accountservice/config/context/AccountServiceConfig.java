package faang.school.accountservice.config.context;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan(basePackages = "faang.school.accountservice.entity")
@EnableJpaRepositories(basePackages = "faang.school.accountservice.repository")
@EnableTransactionManagement
public class AccountServiceConfig {}
