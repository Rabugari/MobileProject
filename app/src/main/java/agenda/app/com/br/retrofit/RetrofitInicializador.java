package agenda.app.com.br.retrofit;

import com.google.firebase.database.Logger;

import agenda.app.com.br.service.AlunoService;
import agenda.app.com.br.service.DispositivoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Massao on 18/07/2017.
 */

public class RetrofitInicializador {

    private final Retrofit retrofit;

    public RetrofitInicializador(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);

        retrofit = new Retrofit.Builder().baseUrl("http://192.168.0.14:8080/api/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(builder.build())
                .build();

    }

    public AlunoService getAlunoService() {
        return retrofit.create(AlunoService.class);
    }

    public DispositivoService getDispositivoService() {
        return retrofit.create(DispositivoService.class);
    }
}
