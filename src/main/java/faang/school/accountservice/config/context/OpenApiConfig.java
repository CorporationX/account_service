package faang.school.accountservice.config.context;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenApi() {
        return new OpenAPI()
                .info(new Info().title("Account service API")
                        .description("Welcome to API documentation for account service")
                        .version("v1.0"));
    }
}