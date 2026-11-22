package pl.lab.roombooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI roomBookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Room Booking API")
                        .description("REST API do zarządzania salami konferencyjnymi i dydaktycznymi (Laboratorium 1)")
                        .version("v1.0"));
    }
}
