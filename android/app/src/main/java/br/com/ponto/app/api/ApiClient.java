package br.com.ponto.app.api;

import br.com.ponto.app.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static ApiService api;

    private ApiClient() {
    }

    public static ApiService api() {
        if (api == null) {
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request.Builder requisicao = chain.request().newBuilder();
                        if (Sessao.logado() && chain.request().header("Authorization") == null) {
                            requisicao.header("Authorization", "Bearer " + Sessao.token());
                        }
                        return chain.proceed(requisicao.build());
                    })
                    .build();
            api = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return api;
    }
}
