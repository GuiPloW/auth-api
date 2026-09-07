# 🔐 Auth API

API REST desenvolvida com **Spring Boot** para autenticação de usuários utilizando **JWT** e controle de acesso baseado em **roles (RBAC)**.

O projeto implementa cadastro, login, autenticação com Access Token, Refresh Token, logout com revogação de token e autorização de rotas para usuários e administradores.

---

## 🚀 Tecnologias

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- JWT (JSON Web Token)
- PostgreSQL
- H2 Database
- Maven
- Docker
- Docker Compose
- Swagger / OpenAPI
- JUnit
- MockMvc

---

## 📌 Funcionalidades

- Cadastro de usuários
- Login com e-mail e senha
- Senhas armazenadas com BCrypt
- Autenticação com JWT
- Access Token
- Refresh Token
- Renovação de Access Token
- Revogação de Refresh Token no logout
- Controle de acesso baseado em roles (RBAC)
- Roles `USER` e `ADMIN`
- Rotas protegidas
- Tratamento global de exceções
- Respostas padronizadas para erros
- Validação de dados
- Documentação com Swagger
- Persistência com PostgreSQL
- Ambiente containerizado com Docker
- Testes de integração

---

## 📁 Estrutura do projeto

O projeto foi organizado em camadas para separar as responsabilidades da aplicação:

```text
src/main/java/br/com/guilherme/authapi
│
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
├── service
└── AuthApiApplication.java
```

### Responsabilidades

- `config` — configurações da aplicação e OpenAPI
- `controller` — endpoints REST
- `dto` — objetos de entrada e saída da API
- `exception` — exceções e tratamento global de erros
- `model` — entidades e enums
- `repository` — acesso ao banco de dados
- `security` — JWT, filtros e handlers do Spring Security
- `service` — regras de negócio

---

## 🔑 Fluxo de autenticação

O fluxo principal de autenticação funciona da seguinte forma:

```text
Usuário
   │
   │ POST /auth/login
   ▼
AuthController
   │
   ▼
AuthService
   │
   │ Valida e-mail e senha
   ▼
JWT + Refresh Token
   │
   ▼
Cliente
```

Após realizar o login corretamente, a API retorna:

```json
{
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token",
  "tokenType": "Bearer"
}
```

O Access Token deve ser enviado nas rotas protegidas através do header:

```http
Authorization: Bearer SEU_TOKEN
```

---

## 👥 Controle de acesso (RBAC)

A API possui duas roles:

```text
USER
ADMIN
```

### USER

Usuários com role `USER` podem acessar as rotas destinadas a usuários autenticados.

Exemplo:

```http
GET /users/me
```

### ADMIN

Usuários com role `ADMIN` possuem acesso às rotas administrativas.

Exemplo:

```http
GET /admin/users
```

Caso um usuário com role `USER` tente acessar uma rota administrativa, a API retorna:

```http
403 Forbidden
```

---

# 📡 Endpoints

## Autenticação

### Cadastrar usuário

```http
POST /auth/register
```

Exemplo de requisição:

```json
{
  "name": "Guilherme",
  "email": "guilherme@email.com",
  "password": "123456"
}
```

Resposta esperada:

```http
201 Created
```

---

### Login

```http
POST /auth/login
```

Exemplo de requisição:

```json
{
  "email": "guilherme@email.com",
  "password": "123456"
}
```

Exemplo de resposta:

```json
{
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token",
  "tokenType": "Bearer"
}
```

---

### Renovar Access Token

```http
POST /auth/refresh
```

Exemplo de requisição:

```json
{
  "refreshToken": "refresh-token"
}
```

Exemplo de resposta:

```json
{
  "accessToken": "novo-jwt-token",
  "tokenType": "Bearer"
}
```

---

### Logout

```http
POST /auth/logout
```

Exemplo de requisição:

```json
{
  "refreshToken": "refresh-token"
}
```

O Refresh Token informado é revogado e não poderá mais ser utilizado para gerar novos Access Tokens.

Resposta:

```http
204 No Content
```

---

## 👤 Usuário autenticado

### Consultar usuário atual

```http
GET /users/me
```

É necessário enviar o Access Token:

```http
Authorization: Bearer SEU_ACCESS_TOKEN
```

Exemplo de resposta:

```json
{
  "id": 1,
  "name": "Guilherme",
  "email": "guilherme@email.com",
  "role": "USER"
}
```

---

## 🛡️ Administração

### Listar usuários

```http
GET /admin/users
```

Esta rota está disponível apenas para usuários com a role:

```text
ADMIN
```

Também é necessário enviar:

```http
Authorization: Bearer SEU_ACCESS_TOKEN
```

A rota retorna a lista de usuários cadastrados na aplicação.

---

## ⚠️ Tratamento de erros

A API possui tratamento global de exceções e respostas padronizadas.

Exemplo de resposta para credenciais inválidas:

```json
{
  "timestamp": "2026-09-07T15:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "E-mail ou senha inválidos",
  "path": "/auth/login"
}
```

Principais códigos HTTP utilizados:

| Status | Descrição |
|---|---|
| `200` | Requisição realizada com sucesso |
| `201` | Recurso criado com sucesso |
| `204` | Requisição concluída sem conteúdo de resposta |
| `400` | Dados da requisição inválidos |
| `401` | Não autenticado ou credenciais inválidas |
| `403` | Usuário autenticado sem permissão |
| `409` | Recurso já existente |

---

## 📖 Swagger / OpenAPI

A API possui documentação interativa utilizando **Swagger/OpenAPI**.

Após iniciar a aplicação, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

No Swagger é possível visualizar e testar os endpoints da aplicação.

Para acessar uma rota protegida:

1. Faça login através de `/auth/login`
2. Copie o `accessToken`
3. Clique em **Authorize**
4. Informe o token
5. Execute a rota protegida desejada

---

## 🔐 Variáveis de ambiente

Informações sensíveis e configurações externas não são armazenadas diretamente no código-fonte.

O projeto utiliza as seguintes variáveis:

```env
DB_URL=jdbc:postgresql://postgres:5432/auth_api
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password

JWT_SECRET=your_jwt_secret_at_least_32_bytes

JWT_EXPIRATION=3600000
REFRESH_TOKEN_EXPIRATION=604800000
```

Utilize o arquivo:

```text
.env.example
```

como modelo para criar seu próprio:

```text
.env
```

> O arquivo `.env` contém informações sensíveis e não deve ser enviado para o GitHub.

---

## 🐳 Executando com Docker

### 1. Clone o repositório

```bash
git clone https://github.com/GuiPloW/auth-api.git
```

### 2. Entre no diretório

```bash
cd auth-api
```

### 3. Crie o arquivo `.env`

No Linux/macOS:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Depois configure sua senha do PostgreSQL e uma chave JWT forte.

### 4. Inicie os containers

```bash
docker compose up --build
```

O Docker Compose iniciará:

- API Spring Boot
- PostgreSQL

A API ficará disponível em:

```text
http://localhost:8080
```

E o Swagger em:

```text
http://localhost:8080/swagger-ui/index.html
```

### 5. Encerrar os containers

```bash
docker compose down
```

---

## 💻 Executando localmente

Para executar sem Docker, é necessário possuir:

- Java 21
- PostgreSQL
- Maven ou Maven Wrapper

A aplicação utiliza:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
```

Portanto, configure as variáveis de ambiente necessárias para o seu ambiente local.

Depois execute:

### Linux/macOS

```bash
./mvnw spring-boot:run
```

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

---

## 🧪 Testes

O projeto possui testes automatizados utilizando:

- JUnit
- MockMvc
- Spring Boot Test
- H2 Database

O banco H2 é utilizado durante os testes para que a suíte não dependa do PostgreSQL utilizado pela aplicação.

### Executar os testes

Linux/macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

Os testes cobrem cenários como:

```text
Cadastro válido                  → 201 Created
E-mail duplicado                 → 409 Conflict
Login válido                     → 200 OK
Login inválido                   → 401 Unauthorized
Rota protegida sem JWT           → 401 Unauthorized
USER acessando rota ADMIN        → 403 Forbidden
ADMIN acessando rota ADMIN       → 200 OK
Refresh Token válido             → 200 OK
Refresh Token inválido           → 401 Unauthorized
```

Além dos testes de integração, o projeto também verifica o carregamento do contexto da aplicação.

---

## 🛡️ Segurança

Entre as medidas de segurança implementadas estão:

- Hash de senhas utilizando BCrypt
- Autenticação stateless com Spring Security
- Tokens JWT assinados
- Expiração de Access Tokens
- Refresh Tokens persistidos no banco
- Revogação de Refresh Token durante o logout
- Controle de acesso baseado em roles (RBAC)
- Separação entre permissões `USER` e `ADMIN`
- Variáveis sensíveis externas ao código-fonte
- `.env` ignorado pelo Git
- Validação dos dados recebidos pela API
- Tratamento personalizado para erros `401` e `403`

---

## 🔄 Access Token e Refresh Token

O projeto utiliza dois tipos de token.

### Access Token

Utilizado para autenticar as requisições realizadas nas rotas protegidas.

Possui duração menor e deve ser enviado através do header:

```http
Authorization: Bearer ACCESS_TOKEN
```

### Refresh Token

Possui duração maior e permite gerar um novo Access Token sem que o usuário precise informar novamente e-mail e senha.

Fluxo:

```text
Login
  │
  ├── Access Token
  │
  └── Refresh Token
          │
          ▼
    POST /auth/refresh
          │
          ▼
   Novo Access Token
```

Ao realizar logout, o Refresh Token é removido/revogado e deixa de ser válido.

---

## 🗄️ Banco de dados

A aplicação utiliza **PostgreSQL** como banco de dados principal.

As principais informações persistidas são:

```text
users
│
├── id
├── name
├── email
├── password
└── role


refresh_tokens
│
├── id
├── token
├── expires_at
└── user_id
```

O relacionamento permite associar cada Refresh Token ao respectivo usuário.

Durante os testes automatizados é utilizado **H2 em memória**.

---

## 🔮 Melhorias futuras

Algumas evoluções possíveis para o projeto:

- Flyway para versionamento e migrations do banco
- Refresh Token Rotation
- Confirmação de e-mail
- Recuperação de senha
- Rate limiting
- Auditoria de tentativas de login
- Blacklist de Access Tokens
- OAuth2
- Login com provedores externos
- CI/CD com GitHub Actions
- Deploy em ambiente cloud
- Testes adicionais de segurança

---

## 🎯 Objetivo do projeto

Este projeto foi desenvolvido com o objetivo de praticar e demonstrar conhecimentos em desenvolvimento backend com Java, incluindo:

- Desenvolvimento de APIs REST
- Spring Boot
- Spring Security
- Autenticação e autorização
- JWT
- RBAC
- Persistência de dados
- PostgreSQL
- Tratamento de exceções
- Testes de integração
- Docker
- Documentação de APIs

---

## 👨‍💻 Autor

Desenvolvido por **Guilherme**.

GitHub: https://github.com/GuiPloW