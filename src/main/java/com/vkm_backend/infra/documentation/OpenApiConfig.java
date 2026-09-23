package com.vkm_backend.infra.documentation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "KVM Backend API",
                version = "v1",
                description = "API REST do KVM para cadastro de usuários, autenticação e gerenciamento futuro de grupos, membros, times, partidas e torneios.",
                contact = @Contact(
                        name = "Equipe KVM",
                        email = "suporte@kvm.local"
                ),
                license = @License(
                        name = "Uso interno do projeto KVM"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Ambiente local ou Docker Compose"
                )
        },
        tags = {
                @Tag(
                        name = "Autenticação",
                        description = "Login, renovação de tokens e encerramento de sessões"
                ),
                @Tag(
                        name = "Usuários",
                        description = "Cadastro, consulta e atualização de usuários"
                ),
                @Tag(
                        name = "Grupos",
                        description = "Operações futuras de grupos e participação de membros"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Access token JWT. Informe apenas o token; o Swagger adicionará o prefixo Bearer automaticamente."
)
public class OpenApiConfig {
}
