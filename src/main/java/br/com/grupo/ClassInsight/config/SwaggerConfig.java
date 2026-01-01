package br.com.grupo.ClassInsight.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI classInsightOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ClassInsight API")
                        .description("Plataforma em nuvem baseada em arquitetura serverless para coleta de feedbacks educacionais.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("ClassInsight Team")
                                .email("contato@classinsight.com")
                                .url("https://classinsight.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/classinsight")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.classinsight.com")
                                .description("Servidor de Produção")
                ));
    }
}
