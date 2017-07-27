package agenda.app.com.br.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import agenda.app.com.br.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Massao on 19/07/2017.
 *
 * Serviço que irá enviar o token para a firebase (obs: o token só é criado no momento da instalação ou quando
 * a memória é limpa)
 */
public class AgendaInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FIREBASE", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        //envia o token para o servidor/firebase
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String refreshedToken) {
        Call<Void> call = new RetrofitInicializador().getDispositivoService().enviaToken(refreshedToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("FIREBASE", "onResponse: Tolkien enviado " + refreshedToken);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FIREBASE", "onResponse: Tolkien erro " + refreshedToken);
            }
        });
    }
}
