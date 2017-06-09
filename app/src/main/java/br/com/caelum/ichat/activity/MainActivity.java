package br.com.caelum.ichat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import br.com.caelum.ichat.app.ChatApplication;
import br.com.caelum.ichat.adapter.MensagemAdapter;
import br.com.caelum.ichat.callback.EnviarMensagemCallback;
import br.com.caelum.ichat.callback.OuvirMensagemCallback;
import br.com.caelum.ichat.component.ChatComponent;
import br.com.caelum.ichat.event.FailureEvent;
import br.com.caelum.ichat.event.MensagemEvent;
import br.com.caelum.ichat.modelo.Mensagem;
import br.com.caelum.ichat.service.ChatService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caelum.com.br.ichat_alura.R;
import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private int idDoCliente;

    private List<Mensagem> mensagens;
    //Os frameworks de injeção de dependencias não trabalham com variaveis privadas, portanto

    @BindView(R.id.et_texto)
    EditText editText;

    @BindView(R.id.btn_enviar)
    Button button;

    @BindView(R.id.mensagem)
    ListView listaDeMensagens;

    @BindView(R.id.iv_avatar_usuario)
    ImageView avatarUsuario;

    @Inject
    ChatService chatService;

    @Inject
    Picasso picasso;

    private ChatComponent component;

    @Inject
    EventBus eventBus;

    @Inject
    InputMethodManager manager;

    /*
    substituido pelo eventbus
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Mensagem mensagem = (Mensagem) intent.getSerializableExtra("mensagem");
            colocaNaLista(mensagem);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idDoCliente = -1;
        //para o butterknife funcionar, preciso declarar
        ButterKnife.bind(this);

        //biblioteca picasso carrega uma imagem baseado em uma url
        picasso.with(this).load("http://api.adorable.io/avatars/" + idDoCliente + ".png").into(avatarUsuario);

        ChatApplication app = (ChatApplication) getApplication();
        component = app.getComponent();
        component.inject(this);

        //Defino que essa classe ouvirá o eventbus
        eventBus.register(this);

        if(savedInstanceState!=null){
            mensagens = (List<Mensagem>) savedInstanceState.getSerializable("mensagens");
        } else{
            mensagens = new ArrayList<>();
        }

        List<Mensagem> mensagens = mensagens = new ArrayList<>();

        MensagemAdapter adapter = new MensagemAdapter(idDoCliente, mensagens, this);
        listaDeMensagens.setAdapter(adapter);

        /*ChatModule chatApplication = (ChatModule) getApplication();
        this.chatService = chatApplication.getChatService();*/

        ouvirMensagem(new MensagemEvent());

        /**
         * SUBSTITUIDO PELO EVENT BUS
         *
         * Declaro um listener de evento para quando chegar uma mensagem,
         * o receiver recupera a mensagem e coloca na lista
         *
         *  LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
         localBroadcastManager.registerReceiver(receiver, new IntentFilter("nova_mensagem"));
         */

    }

    @Subscribe
    public void ouvirMensagem(MensagemEvent mensagemEvent) {
        Call<Mensagem> call = chatService.ouvirMensagens();
        call.enqueue(new OuvirMensagemCallback(eventBus,this));
    }

    @OnClick(R.id.btn_enviar)
    public void enviarMensagem(){
        Mensagem mensagem = new Mensagem(idDoCliente, editText.getText().toString());
        chatService.enviar(mensagem)
                .enqueue(new EnviarMensagemCallback());

        //limpa o texto e esconde o teclado
        editText.getText().clear();
        manager.hideSoftInputFromInputMethod(editText.getWindowToken(), 0);
    }

    /**
     * Subscribe é do eventBus - Toda vez que ele publicar um mensagemEvent, esse método irá ouvir
     * @param mensagemEvent
     */
    @Subscribe
    public void colocaNaLista(MensagemEvent mensagemEvent) {
        mensagens.add(mensagemEvent.getMensagem());
        MensagemAdapter adapter = new MensagemAdapter(idDoCliente, mensagens, this);
        listaDeMensagens.setAdapter(adapter);

        //ouvirMensagem();
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(receiver);
        */
        eventBus.unregister(this);
    }

    /**
     * Para lidar com as falhas de acordo com o EventBus
     * @param failureEvent
     */
    @Subscribe
    public void lidarCom(FailureEvent failureEvent){
        ouvirMensagem(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("mensagens", (ArrayList<Mensagem>) mensagens);
        //outra forma
        //outState.putParcelableArrayList("mensagens", (ArrayList<Mensagem>) mensagens);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
