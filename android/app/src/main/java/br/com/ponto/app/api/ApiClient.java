package br.com.ponto.app.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static final String URL_BASE = "http://10.0.2.2:8080/";

    private static ApiService api;

    private ApiClient() {
    }

    public static ApiService api() {
        if (api == null) {
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request.Builder requisicao = chain.request().newBuilder();
                        if (Sessao.logado()) {
                            requisicao.header("Authorization", "Bearer " + Sessao.token());
                        }
                        return chain.proceed(requisicao.build());
                    })
                    .build();
            api = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return api;
    }
}
