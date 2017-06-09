package br.com.caelum.ichat.callback;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.greenrobot.eventbus.EventBus;

import br.com.caelum.ichat.activity.MainActivity;
import br.com.caelum.ichat.event.FailureEvent;
import br.com.caelum.ichat.event.MensagemEvent;
import br.com.caelum.ichat.modelo.Mensagem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OuvirMensagemCallback implements Callback<Mensagem> {

    private EventBus eventBus;
    private Context context;

    public OuvirMensagemCallback(EventBus eventBus, Context context) {
        this.eventBus = eventBus;
        this.context = context;
    }

    @Override
    public void onResponse(Call<Mensagem> call, Response<Mensagem> response){
        if(response.isSuccessful()) {
            Mensagem mensagem = response.body();

            eventBus.post(new MensagemEvent(mensagem));

            /*
            SUBSTITUIDO PELO EVENT BUS

            Ao invés de ficar dependendo de uma activity (e se tiver mais?)
            Gero um evento para quem quiser ouvi-lo executar algum tipo de ação
            Para poder recuperar o evento, deve ser criado um intentFilter com o mesmo nome da Intent
            aqui criada.
            {@link MainActivity}

            Intent intent = new Intent("nova_mensagem");
            intent.putExtra("mensagem", mensagem);

            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            localBroadcastManager.sendBroadcast(intent);
             */


            /*
            context.colocaNaLista(mensagem);
            context.ouvirMensagem();
            */
        }
    }

    @Override
    public void onFailure(Call<Mensagem> call, Throwable t) {
        //context.ouvirMensagem();
        eventBus.post(new FailureEvent());
    }
}
