# ClassInsight - Resumo do Projeto

## 📋 Estrutura Criada

O projeto ClassInsight foi estruturado com uma arquitetura em camadas seguindo as melhores práticas do Spring Boot.

### 📁 Pacotes Criados

```
src/main/java/br/com/grupo/ClassInsight/
├── model/          # Entidades JPA
├── dto/            # Data Transfer Objects
├── repository/     # Interfaces Spring Data JPA
├── service/        # Lógica de negócio
├── controller/     # Endpoints REST
└── exception/      # Exceções customizadas
```

---

## 📚 Classes Criadas

### MODEL (Entidades)
1. **Usuario.java** - Usuários do sistema (alunos, professores, administradores)
2. **TipoUsuario.java** - Enum com tipos de usuários
3. **Turma.java** - Turmas/Classes
4. **Feedback.java** - Feedbacks enviados por alunos
5. **TipoFeedback.java** - Enum com tipos de feedback
6. **StatusFeedback.java** - Enum com status do feedback
7. **MatriculaTurma.java** - Relação aluno-turma

### DTO (Data Transfer Objects)
1. **UsuarioCriacaoDTO.java** - DTO para criar/atualizar usuários
2. **UsuarioDTO.java** - DTO para retornar usuários
3. **TurmaCriacaoDTO.java** - DTO para criar/atualizar turmas
4. **TurmaDTO.java** - DTO para retornar turmas
5. **FeedbackCriacaoDTO.java** - DTO para criar feedbacks
6. **FeedbackDTO.java** - DTO para retornar feedbacks
7. **RespostaFeedbackDTO.java** - DTO para responder feedbacks

### REPOSITORY
1. **UsuarioRepository.java** - Acesso a dados de usuários
2. **TurmaRepository.java** - Acesso a dados de turmas
3. **FeedbackRepository.java** - Acesso a dados de feedbacks
4. **MatriculaTurmaRepository.java** - Acesso a dados de matrículas

### SERVICE
1. **UsuarioService.java** - Lógica de usuários (CRUD)
2. **TurmaService.java** - Lógica de turmas (CRUD)
3. **FeedbackService.java** - Lógica de feedbacks
4. **MatriculaTurmaService.java** - Lógica de matrículas

### CONTROLLER
1. **UsuarioController.java** - Endpoints de usuários
2. **TurmaController.java** - Endpoints de turmas
3. **FeedbackController.java** - Endpoints de feedbacks
4. **MatriculaTurmaController.java** - Endpoints de matrículas

### EXCEPTION
1. **ResourceNotFoundException.java** - Exceção para recurso não encontrado
2. **DuplicateResourceException.java** - Exceção para recurso duplicado
3. **InvalidOperationException.java** - Exceção para operação inválida
4. **GlobalExceptionHandler.java** - Tratamento global de exceções
5. **ErrorResponse.java** - DTO de resposta de erro

---

## 🚀 Endpoints Principais

### Usuários
- `POST /api/usuarios` - Criar usuário
- `GET /api/usuarios/{id}` - Obter usuário
- `GET /api/usuarios` - Listar todos
- `PUT /api/usuarios/{id}` - Atualizar
- `DELETE /api/usuarios/{id}` - Deletar

### Turmas
- `POST /api/turmas` - Criar turma
- `GET /api/turmas/{id}` - Obter turma
- `GET /api/turmas/professor/{professorId}` - Turmas do professor
- `PUT /api/turmas/{id}` - Atualizar
- `DELETE /api/turmas/{id}` - Deletar

### Feedbacks
- `POST /api/feedbacks/aluno/{alunoId}` - Criar feedback
- `GET /api/feedbacks/{id}` - Obter feedback
- `GET /api/feedbacks/turma/{turmaId}` - Feedbacks da turma
- `GET /api/feedbacks/status/aberto` - Feedbacks abertos
- `POST /api/feedbacks/{id}/responder/{professorId}` - Responder feedback
- `PUT /api/feedbacks/{id}/fechar` - Fechar feedback

### Matrículas
- `POST /api/matriculas/aluno/{alunoId}/turma/{turmaId}` - Matricular
- `GET /api/matriculas/aluno/{alunoId}` - Turmas do aluno
- `GET /api/matriculas/turma/{turmaId}` - Alunos da turma
- `DELETE /api/matriculas/aluno/{alunoId}/turma/{turmaId}` - Desmatricular

---

## 🔧 Configurações

### Dependências Adicionadas
- Spring Boot Web
- Spring Data JPA
- Spring Validation
- Lombok
- H2 Database (in-memory)
- Jakarta Persistence API

### Banco de Dados
- **Engine**: H2 (in-memory para desenvolvimento)
- **URL**: `jdbc:h2:mem:classinsightdb`
- **Console**: `http://localhost:8080/classinsight/h2-console`

### Server
- **Port**: 8080
- **Context Path**: `/classinsight`

---

## ✅ Funcionalidades Implementadas

### Autenticação & Autorização
- ✅ Criação de usuários com tipos (ALUNO, PROFESSOR, ADMINISTRADOR)
- ✅ Validação de dados com Bean Validation
- ✅ Gerenciamento de usuários ativos/inativos

### Gerenciamento de Turmas
- ✅ Criação e gerenciamento de turmas
- ✅ Geração automática de código único para turmas
- ✅ Listagem de turmas por professor
- ✅ Ativação/desativação de turmas

### Sistema de Feedbacks
- ✅ Criação de feedbacks com tipos variados
- ✅ Status de feedback (ABERTO, EM_ANALISE, RESPONDIDO, FECHADO)
- ✅ Resposta de feedbacks por professores
- ✅ Histórico de feedbacks por aluno, turma ou status

### Matrículas
- ✅ Matrícula de alunos em turmas
- ✅ Verificação de matrículas
- ✅ Desmatrícula com histórico
- ✅ Listagem de alunos por turma

### Tratamento de Erros
- ✅ Exceções customizadas
- ✅ Resposta padronizada de erros
- ✅ Validação de entrada com mensagens claras

---

## 📝 Como Usar

### 1. Compilar
```bash
mvn clean compile
```

### 2. Executar
```bash
mvn spring-boot:run
```

### 3. Testar Endpoints
```bash
curl -X POST http://localhost:8080/classinsight/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "email": "aluno@example.com",
    "nome": "João Silva",
    "senha": "senha123",
    "tipo_usuario": "ALUNO"
  }'
```

---

## 📖 Documentação Completa

Consulte o arquivo `API_DOCUMENTATION.md` para:
- Detalhes de todos os endpoints
- Exemplos de requisições
- Formatos de resposta
- Códigos HTTP
- Validações

---

## 🎯 Próximos Passos (Sugestões)

1. **Segurança**
   - Implementar JWT para autenticação
   - Adicionar Spring Security
   - Criptografar senhas com BCrypt

2. **Banco de Dados**
   - Migrar de H2 para PostgreSQL/MySQL
   - Implementar Flyway para migrations
   - Adicionar índices otimizados

3. **Testes**
   - Testes unitários com JUnit 5
   - Testes de integração
   - Testes de API com RestAssured

4. **Observabilidade**
   - Implementar logging estruturado
   - Adicionar métricas com Micrometer
   - Health checks

5. **API Avançada**
   - Paginação e filtering
   - Busca full-text
   - Rate limiting

---

## 📄 Arquivos Importantes

- `pom.xml` - Configuração Maven com dependências
- `application.properties` - Configurações da aplicação
- `API_DOCUMENTATION.md` - Documentação completa da API
- `README_PROJETO.md` - Este arquivo

