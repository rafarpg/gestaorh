package br.com.ponto.app.api;

import java.util.List;

import br.com.ponto.app.model.DiaPonto;
import br.com.ponto.app.model.Ferias;
import br.com.ponto.app.model.Funcionario;
import br.com.ponto.app.model.Historico;
import br.com.ponto.app.model.LoginRequest;
import br.com.ponto.app.model.LoginResponse;
import br.com.ponto.app.model.Marcacao;
import br.com.ponto.app.model.PontoHoje;
import br.com.ponto.app.model.PontoManual;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/logout")
    Call<Void> logout(@Header("Authorization") String authorization);

    @GET("api/ponto/hoje")
    Call<PontoHoje> pontoHoje();

    @POST("api/ponto")
    Call<Marcacao> baterPonto();

    @PUT("api/ponto/manual")
    Call<DiaPonto> lancarPontoManual(@Body PontoManual pontoManual);

    @GET("api/ponto/historico")
    Call<Historico> historico(@Query("mes") String mes, @Query("funcionarioId") Long funcionarioId);

    @GET("api/funcionarios")
    Call<List<Funcionario>> listarFuncionarios();

    @POST("api/funcionarios")
    Call<Funcionario> criarFuncionario(@Body Funcionario funcionario);

    @PUT("api/funcionarios/{id}")
    Call<Funcionario> atualizarFuncionario(@Path("id") long id, @Body Funcionario funcionario);

    @DELETE("api/funcionarios/{id}")
    Call<Void> excluirFuncionario(@Path("id") long id);

    @GET("api/ferias/minhas")
    Call<List<Ferias>> minhasFerias();

    @POST("api/ferias")
    Call<Ferias> solicitarFerias(@Body Ferias ferias);

    @GET("api/ferias/pendentes")
    Call<List<Ferias>> feriasPendentes();

    @PUT("api/ferias/{id}/{status}")
    Call<Ferias> decidirFerias(@Path("id") long id, @Path("status") String status);
}
