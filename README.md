# Ponto RH


 https://youtu.be/3hsUNN4aNSM


App de registro de ponto com banco de horas. O front-end é Android nativo (Java), o back-end é Java
(Spring Boot) e os dois se comunicam por uma API REST.

## Funcionalidades

**Funcionário**
- Login com e-mail e senha
- Bater ponto: entrada, saída para almoço, retorno e saída, com o horário do servidor
- Ponto manual: lançar os horários de um dia esquecido, informando o motivo
- Histórico do mês com horas trabalhadas e saldo de cada dia
- Banco de horas (jornada de 8h por dia)
- Solicitação de férias

**RH** (tudo do funcionário e mais)
- Cadastro, edição e exclusão de funcionários
- Histórico de ponto de qualquer funcionário
- Aprovar ou recusar férias

## Arquitetura

```
App Android  --HTTP/JSON-->  API Spring Boot  -->  Banco H2
```

- **App (`android/`)**: telas em XML no padrão MVVM (a Activity mostra os dados e o ViewModel busca na API
  usando o Retrofit).
- **API (`backend/`)**: o controller recebe a requisição, o service aplica as regras e o repository acessa o banco.
- O login devolve um token, que o app envia nas demais requisições. As rotas do RH são bloqueadas para os outros usuários.

## Como rodar

Precisa do **JDK 21**. Maven e Gradle não precisam ser instalados, porque o projeto usa os wrappers (`mvnw` e `gradlew`).

### 1. Back-end

Inicie o servidor:

```
cd backend
mvnw.cmd spring-boot:run
```

(Linux/macOS: `./mvnw spring-boot:run`)

Na primeira execução são criados os usuários `rh@empresa.com` (RH) e `maria@empresa.com` (funcionária).

### 2. App Android

O app acessa o servidor em `http://10.0.2.2:8080`, que é o seu computador visto de dentro do emulador.
Para usar um celular de verdade, troque esse endereço pelo IP do computador em `android/app/src/main/java/br/com/ponto/app/api/ApiClient.java`.

**Opção A: com o Android Studio**
1. Abra a pasta `android` no Android Studio e espere o Gradle sincronizar.
2. Crie um emulador (Device Manager → Pixel com API 36) e clique em **Run**.

**Opção B: sem o Android Studio**
1. Instale o Android SDK Command-line Tools, crie a variável de ambiente `ANDROID_HOME` apontando para a
   pasta do SDK e baixe os pacotes:
   ```
   sdkmanager "platform-tools" "emulator" "platforms;android-36" "build-tools;36.0.0" "system-images;android-36;google_apis;x86_64"
   avdmanager create avd -n Pixel -k "system-images;android-36;google_apis;x86_64" -d pixel
   ```
   (em versões novas do `sdkmanager`, use `/` no lugar de `;` nos nomes dos pacotes)
2. Ligue o emulador com `emulator -avd Pixel` e, em outro terminal, instale o app:
   ```
   cd android
   gradlew.bat installDebug
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
- O banco fica no arquivo `backend/data/ponto.mv.db`. Apagando esse arquivo (com o servidor parado), o banco
  volta ao estado inicial na próxima execução.
