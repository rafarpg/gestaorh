# Ponto RH

**Vídeo de demonstração:** [<https://youtu.be/3hsUNN4aNSM>](https://youtu.be/CNXY82oMzZc)

App de registro de ponto com banco de horas. O front-end é Android nativo (Java), o back-end é Java
(Spring Boot) e os dois se comunicam por uma API REST.

## Funcionalidades

**Funcionário**
- Login com e-mail e senha (e logout com invalidação do token no servidor)
- Bater ponto: entrada, saída para almoço, retorno e saída, com o horário do servidor
- Ponto manual: lançar ou corrigir os horários de um dia, informando o motivo
- Histórico do mês com horas trabalhadas e saldo de cada dia
- Banco de horas (jornada de 8h por dia)
- Solicitação e acompanhamento de férias

**RH** (tudo do funcionário e mais)
- Cadastro, edição e exclusão de funcionários
- Histórico de ponto de qualquer funcionário
- Aprovação ou recusa de solicitações de férias pendentes

## Arquitetura

```
App Android  --HTTP/JSON (REST)-->  API Spring Boot  -->  Banco H2
```

- **App (`android/`)**: telas em XML no padrão MVVM (`Activity` + `ViewModel` + `LiveData`), `ViewBinding` e consumo REST com Retrofit/OkHttp.
- **API (`backend/`)**: arquitetura em camadas (`controller`, `service`, `repository`, `model`, `dto`, `config`, `exception`) com validação via Bean Validation e tratamento centralizado de erros.
- **Autenticação e autorização**: o login devolve um token `Bearer`, enviado no cabeçalho `Authorization` das demais requisições. As rotas administrativas exigem perfil `RH`.

### Endpoints REST

| Método | Endpoint | Perfil | Descrição |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | Público | Autentica usuário e retorna token |
| `POST` | `/api/auth/logout` | Autenticado | Invalida o token atual |
| `GET` | `/api/ponto/hoje` | Autenticado | Marcações de hoje, próxima batida e banco de horas |
| `POST` | `/api/ponto` | Autenticado | Registra a próxima marcação do dia (`201`) |
| `PUT` | `/api/ponto/manual` | Autenticado | Lança/substitui as 4 marcações de uma data com motivo |
| `GET` | `/api/ponto/historico` | Autenticado / RH | Histórico mensal (`mes`, `data` e `funcionarioId` para RH) |
| `GET` | `/api/ferias/minhas` | Autenticado | Lista férias do usuário logado |
| `POST` | `/api/ferias` | Autenticado | Solicita período de férias (`201`) |
| `GET` | `/api/ferias/pendentes` | RH | Lista solicitações de férias pendentes |
| `PUT` | `/api/ferias/{id}/{status}` | RH | Aprova (`APROVADA`) ou recusa (`RECUSADA`) solicitação pendente |
| `GET` | `/api/funcionarios` | RH | Lista funcionários |
| `POST` | `/api/funcionarios` | RH | Cadastra funcionário (`201`) |
| `PUT` | `/api/funcionarios/{id}` | RH | Atualiza funcionário |
| `DELETE` | `/api/funcionarios/{id}` | RH | Exclui funcionário e seus registros (`204`) |

## Como buildar e rodar

É necessário ter o **JDK 21** instalado. Maven e Gradle não precisam ser instalados separadamente, pois o projeto utiliza os wrappers (`mvnw` e `gradlew`).

### 1. Back-end

Executar o servidor:

```bash
cd backend
./mvnw spring-boot:run
```

(Windows: `mvnw.cmd spring-boot:run`)

Executar build e testes automatizados:

```bash
cd backend
./mvnw test      # Windows: mvnw.cmd test
./mvnw package   # Windows: mvnw.cmd package
```

**Credenciais iniciais** (criadas automaticamente na primeira execução):

| Perfil | E-mail | Senha |
| --- | --- | --- |
| RH | `rh@empresa.com` | `senha123` |
| Funcionária | `maria@empresa.com` | `senha123` |

> Por simplificação de escopo acadêmico, todos os usuários (incluindo novos funcionários cadastrados pelo RH) utilizam a senha padrão `senha123`.

### 2. App Android

Por padrão, o app acessa o servidor em `http://10.0.2.2:8080/` (o computador local visto de dentro do emulador Android).
Para testar em um celular físico na mesma rede, altere `API_BASE_URL` em `android/app/build.gradle`.

**Opção A: com o Android Studio**
1. Abra a pasta `android` no Android Studio e aguarde a sincronização do Gradle.
2. Crie um emulador (Device Manager → Pixel com API 36) e clique em **Run**.

**Opção B: pela linha de comando (sem Android Studio)**
1. Instale o Android SDK Command-line Tools, defina a variável de ambiente `ANDROID_HOME` apontando para a pasta do SDK e instale os pacotes:
   ```bash
   sdkmanager "platform-tools" "emulator" "platforms;android-36" "build-tools;36.0.0" "system-images;android-36;google_apis;x86_64"
   avdmanager create avd -n Pixel -k "system-images;android-36;google_apis;x86_64" -d pixel
   ```
   (em versões mais novas do `sdkmanager`, use `/` no lugar de `;` nos nomes dos pacotes)
2. Para gerar o APK de debug:
   ```bash
   cd android
   ./gradlew assembleDebug   # Windows: gradlew.bat assembleDebug
   ```
3. Ligue o emulador com `emulator -avd Pixel` e, em outro terminal, instale o app:
   ```bash
   cd android
   ./gradlew installDebug    # Windows: gradlew.bat installDebug
   ```

## Ver o banco de dados

Com o back-end rodando, abra <http://localhost:8080/h2-console> e preencha:

| Campo | Valor |
| --- | --- |
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:file:./data/ponto` |
| User Name | `sa` |
| Password | *(vazio)* |

Clique em **Connect**. As tabelas aparecem na lateral esquerda; clicar numa delas monta o `SELECT`, e é só clicar em **Run**.
Para listar todas as tabelas, rode `SHOW TABLES;`.

O banco tem três tabelas:

```sql
SELECT * FROM FUNCIONARIOS;         -- funcionários e RH
SELECT * FROM REGISTROS_PONTO;      -- batidas de ponto
SELECT * FROM SOLICITACOES_FERIAS;  -- pedidos de férias
```

- Confira a **JDBC URL**: o valor padrão da tela (`jdbc:h2:~/test`) abre um banco vazio.
- O banco fica no arquivo `backend/data/ponto.mv.db`. Apagando esse arquivo (com o servidor parado), o banco volta ao estado inicial na próxima execução.
