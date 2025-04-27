package faang.school.accountservice.client;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerOpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme userHeaderScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("x-user-id")
                .description("Идентификатор пользователя, выполняющего запрос");

        Components components = new Components()
                .addSecuritySchemes("user-id-header", userHeaderScheme);

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("user-id-header");

        return new OpenAPI()
                .info(new Info()
                        .title("Account Service API")
                        .version("1.0")
                        .description("API для управления банковскими счетами"))
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}
