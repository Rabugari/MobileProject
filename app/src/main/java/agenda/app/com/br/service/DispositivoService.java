package agenda.app.com.br.service;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Massao on 19/07/2017.
 */

public interface DispositivoService {

    /**
     * essa é uma url padrão configurada pelo firebase
     * @param token
     * @return
     */
    @POST("firebase/dispositivo")
    Call<Void> enviaToken(@Header("token") String token);
}
