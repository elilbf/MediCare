# MediCare

## Descrição

O MediCare é um sistema de agendamento de consultas médicas, projetado para facilitar a conexão entre pacientes e profissionais de saúde. A plataforma permite que os usuários se cadastrem, agendem, confirmem e cancelem consultas de forma simples e eficiente.

## Arquitetura

O projeto é construído em uma arquitetura de microsserviços, o que proporciona escalabilidade, flexibilidade e manutenibilidade. Cada serviço é responsável por uma parte específica do negócio e se comunica com os outros através de APIs REST e mensageria.

Os principais módulos do sistema são:

*   **User Service:** Responsável pelo gerenciamento de usuários, incluindo cadastro, autenticação e autorização.
*   **Scheduling Service:** Gerencia toda a lógica de agendamento, como criação, consulta, confirmação e cancelamento de consultas.
*   **Notification Service:** Encarregado de enviar notificações aos usuários, como lembretes de consulta e confirmações de agendamento.

## Tecnologias e Dependências

O projeto utiliza as seguintes tecnologias e dependências:

| Tecnologia/Dependência | Versão |
| --- | --- |
| Java | 21 |
| Spring Boot | 3.5.6 |
| Spring Data JPA | |
| Spring Security | |
| Hibernate Validator | |
| H2 Database | 2.3.232 |
| Resilience4j | 2.2.0 |
| Lombok | 1.18.30 |
| Jakarta Validation API | 3.0.2 |
| Springdoc OpenAPI (Swagger) | 2.3.0 |
| JJWT (JSON Web Token) | 0.11.5 |
| Spring Kafka | |
| Docker | |
| Maven | |

## Como Executar o Projeto

Para executar o projeto, você precisará ter o Docker e o Docker Compose instalados. Siga os passos abaixo:

1.  **Clone o repositório:**
    ```bash
    git clone <url-do-repositorio>
    cd MediCare
    ```

2.  **Execute o Docker Compose:**
    ```bash
    docker-compose up --build -d
    ```

Isso irá construir e iniciar todos os serviços e a infraestrutura necessária (banco de dados, message broker, etc.).

## Como Parar e Limpar o Ambiente

Para parar todos os contêineres e remover as imagens Docker, execute os seguintes comandos:

```bash
docker-compose down
docker image rm $(docker image ls -aq)
```

## Equipe

*   Dhebbora Leane Bezerra de Vasconcelos
*   Eli Leite de Brito Filho
*   Emerson Leonardo Oliveira de Lira
*   Lucas de Medeiros França Romero
