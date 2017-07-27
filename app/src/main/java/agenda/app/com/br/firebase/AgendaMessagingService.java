package agenda.app.com.br.firebase;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import agenda.app.com.br.dao.AlunoDAO;
import agenda.app.com.br.dto.AlunoSync;
import agenda.app.com.br.events.AtualizaListaAlunoEvent;
import agenda.app.com.br.sinc.AlunoSincronizador;

/**
 * Created by Massao on 19/07/2017.
 *
 * Mensagem quando um aluno é cadastrado, o firebase manda uma mensagem para o aplicativo.
 * Para isso acontecer, a url do firebase {Chave do servidor do firebase} precisa estar configurado no server
 */
public class AgendaMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("FIREBASE MESSAGE", "onMessageReceived: " + String.valueOf(mensagem));

        //para converter a mensagem
        converteParaAluno(mensagem);
    }

    private void converteParaAluno(Map<String, String> mensagem) {
        String chaveDeAcesso = "alunoSync";
        if(mensagem.containsKey(chaveDeAcesso)){
            String jsonAluno = mensagem.get(chaveDeAcesso);
            //convertendo pq aqui não da para usar o retrofit (Jackson)
            ObjectMapper mapper = new ObjectMapper();
            try {
                AlunoSync alunoSync = mapper.readValue(jsonAluno, AlunoSync.class);
                new AlunoSincronizador(AgendaMessagingService.this).sincroniza(alunoSync);
//                //sim aqui é um contexto
//                AlunoDAO dao = new AlunoDAO(this);
//                dao.sincroniza(alunoSync.getAlunos());
//                dao.close();

                //e agora como vamos comunicar a activity q tem + 1 aluno? via EVENTBUS!
                EventBus eventBus = EventBus.getDefault();
                //Oq significa esse evento?, todos que estiverem inscritos nesses evento, irão executar alguma ação
                eventBus.post(new AtualizaListaAlunoEvent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
