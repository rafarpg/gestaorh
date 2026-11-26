package br.com.ponto;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testes;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class ApiTest {

    @Autowired
    private MockMvc mvc;

    private String tokenRh;
    private String tokenMaria;

    @BeforeEach
    void entrar() throws Exception {
        tokenRh = login("rh@empresa.com");
        tokenMaria = login("maria@empresa.com");
    }

    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"rh@empresa.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Login ou senha incorretos"));
    }

    @Test
    void requisicaoSemTokenRetorna401() throws Exception {
        mvc.perform(get("/api/ponto/hoje")).andExpect(status().isUnauthorized());
    }

    @Test
    void logoutInvalidaToken() throws Exception {
        String token = novoFuncionario("55555555555", "logout@empresa.com");

        chamar(post("/api/auth/logout"), token).andExpect(status().isNoContent());
        chamar(get("/api/ponto/hoje"), token).andExpect(status().isUnauthorized());
    }

    @Test
    void funcionarioNaoAcessaEndpointsDoRh() throws Exception {
        chamar(get("/api/funcionarios"), tokenMaria).andExpect(status().isForbidden());
        chamar(get("/api/ferias/pendentes"), tokenMaria).andExpect(status().isForbidden());
    }

    @Test
    void rhCadastraFuncionarioQueConsegueEntrar() throws Exception {
        String corpo = """
                {"nome":"Joao","cpf":"22222222222","email":"joao@empresa.com",
                 "cargo":"Vendedor","departamento":"Comercial","perfil":"FUNCIONARIO"}
                """;
        chamar(post("/api/funcionarios").contentType(MediaType.APPLICATION_JSON).content(corpo), tokenRh)
                .andExpect(status().isCreated());
        login("joao@empresa.com");

        chamar(post("/api/funcionarios").contentType(MediaType.APPLICATION_JSON).content(corpo), tokenRh)
                .andExpect(status().isConflict());

        String cpfInvalido = corpo.replace("22222222222", "123").replace("joao@empresa.com", "outro@empresa.com");
        chamar(post("/api/funcionarios").contentType(MediaType.APPLICATION_JSON).content(cpfInvalido), tokenRh)
                .andExpect(status().isBadRequest());
    }

    @Test
    void rhNaoPodeExcluirASiMesmoMasPodeExcluirOutro() throws Exception {
        String lista = chamar(get("/api/funcionarios"), tokenRh)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Integer idRh = JsonPath.read(lista, "$[?(@.email == 'rh@empresa.com')].id").toString()
                .replaceAll("[^0-9]", "").isEmpty() ? null
                : Integer.valueOf(JsonPath.read(lista, "$[?(@.email == 'rh@empresa.com')].id").toString().replaceAll("[^0-9]", ""));
        chamar(delete("/api/funcionarios/" + idRh), tokenRh).andExpect(status().isBadRequest());

        String criado = chamar(post("/api/funcionarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Temp\",\"cpf\":\"66666666666\",\"email\":\"temp@empresa.com\",\"cargo\":\"Aux\",\"departamento\":\"RH\",\"perfil\":\"FUNCIONARIO\"}"), tokenRh)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer idTemp = JsonPath.read(criado, "$.id");
        chamar(delete("/api/funcionarios/" + idTemp), tokenRh).andExpect(status().isNoContent());
    }

    @Test
    void pontoSegueASequenciaEBloqueiaAQuintaMarcacao() throws Exception {
        String token = novoFuncionario("33333333333", "ponto@empresa.com");

        chamar(post("/api/ponto"), token).andExpect(status().isCreated()).andExpect(jsonPath("$.tipo").value("ENTRADA"));
        chamar(post("/api/ponto"), token).andExpect(status().isCreated()).andExpect(jsonPath("$.tipo").value("SAIDA_ALMOCO"));
        chamar(post("/api/ponto"), token).andExpect(status().isCreated()).andExpect(jsonPath("$.tipo").value("RETORNO_ALMOCO"));
        chamar(post("/api/ponto"), token).andExpect(status().isCreated()).andExpect(jsonPath("$.tipo").value("SAIDA"));
        chamar(post("/api/ponto"), token).andExpect(status().isBadRequest());
    }

    @Test
    void pontoManualCalculaSaldoEBancoDeHoras() throws Exception {
        String token = novoFuncionario("44444444444", "manual@empresa.com");
        String manual = """
                {"data":"2026-11-24","entrada":"08:00","saidaAlmoco":"12:00",
                 "retornoAlmoco":"13:00","saida":"18:30","motivo":"ESQUECIMENTO"}
                """;
        chamar(put("/api/ponto/manual").contentType(MediaType.APPLICATION_JSON).content(manual), token)
                .andExpect(jsonPath("$.saldoMinutos").value(90));

        chamar(put("/api/ponto/manual").contentType(MediaType.APPLICATION_JSON)
                .content(manual.replace("18:30", "16:00")), token)
                .andExpect(jsonPath("$.saldoMinutos").value(-60));

        chamar(get("/api/ponto/historico?mes=2026-11"), token)
                .andExpect(jsonPath("$.dias.length()").value(1))
                .andExpect(jsonPath("$.bancoHorasMinutos").value(-60));

        chamar(put("/api/ponto/manual").contentType(MediaType.APPLICATION_JSON)
                .content(manual.replace("\"12:00\"", "\"07:00\"")), token)
                .andExpect(status().isBadRequest());
    }

    @Test
    void rhAprovaFerias() throws Exception {
        String resposta = chamar(post("/api/ferias").contentType(MediaType.APPLICATION_JSON)
                .content("{\"dataInicio\":\"2027-01-10\",\"dataFim\":\"2027-01-19\"}"), tokenMaria)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(resposta, "$.id");

        chamar(put("/api/ferias/" + id + "/APROVADA"), tokenMaria).andExpect(status().isForbidden());
        chamar(put("/api/ferias/" + id + "/PENDENTE"), tokenRh).andExpect(status().isBadRequest());
        chamar(put("/api/ferias/" + id + "/INVALIDO"), tokenRh)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").exists());
        chamar(put("/api/ferias/" + id + "/APROVADA"), tokenRh).andExpect(jsonPath("$.status").value("APROVADA"));
        chamar(put("/api/ferias/" + id + "/RECUSADA"), tokenRh).andExpect(status().isBadRequest());
    }

    private String login(String email) throws Exception {
        String resposta = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"" + email + "\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.token");
    }

    private String novoFuncionario(String cpf, String email) throws Exception {
        String corpo = "{\"nome\":\"Teste\",\"cpf\":\"" + cpf + "\",\"email\":\"" + email
                + "\",\"cargo\":\"Analista\",\"departamento\":\"TI\",\"perfil\":\"FUNCIONARIO\"}";
        chamar(post("/api/funcionarios").contentType(MediaType.APPLICATION_JSON).content(corpo), tokenRh)
                .andExpect(status().isCreated());
        return login(email);
    }

    private ResultActions chamar(MockHttpServletRequestBuilder requisicao, String token) throws Exception {
        return mvc.perform(requisicao.header("Authorization", "Bearer " + token));
    }
}
