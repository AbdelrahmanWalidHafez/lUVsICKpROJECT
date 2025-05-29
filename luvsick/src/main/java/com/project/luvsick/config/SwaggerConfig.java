package com.project.luvsick.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LuvSick APIs")
                        .description("API Documentation for LuvSick E-commerce Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dev Team")
                                .email("walidlrahmn5@gmail.com")
                                .url("https://www.luvSickDev-Team.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
