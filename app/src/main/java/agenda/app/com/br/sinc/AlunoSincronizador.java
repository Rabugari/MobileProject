package agenda.app.com.br.sinc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import agenda.app.com.br.activities.ListaAlunosActivity;
import agenda.app.com.br.dao.AlunoDAO;
import agenda.app.com.br.dto.AlunoSync;
import agenda.app.com.br.events.AtualizaListaAlunoEvent;
import agenda.app.com.br.modelo.Aluno;
import agenda.app.com.br.preference.AlunoPreferences;
import agenda.app.com.br.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoSincronizador {

    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private AlunoPreferences preference;

    public AlunoSincronizador(Context context) {
        this.context = context;
        preference = new AlunoPreferences(context);
    }

    @NonNull
    private Callback<AlunoSync> buscaAlunosCallback() {
        return new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                AlunoSync alunoSync = response.body();
                sincroniza(alunoSync);

//                Log.i("versao", "onResponse: " + preference.getVersao());

                bus.post(new AtualizaListaAlunoEvent());
                sincronizaAlunosInternos();
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {
                Log.e("sincronizacao", "onFailure: " + t.getMessage());
                bus.post(new AtualizaListaAlunoEvent());
            }
        };
    }

    public void sincroniza(AlunoSync alunoSync) {
        String versao = alunoSync.getMomentoDaUltimaModificacao();

        Log.i("versao externa",  versao);

        if(temVersaoNova(versao)){
            preference.salvaVersao(versao);

            Log.i("versao atual", preference.getVersao());

            AlunoDAO alunoDAO = new AlunoDAO(context);
            alunoDAO.sincroniza(alunoSync.getAlunos());
            alunoDAO.close();
        }
    }

    private boolean temVersaoNova(String versao) {
        if(!preference.temVersao())
            return true;
        //2017-04-25T09:15:01.222
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date dataExterna = format.parse(versao);
            String versaoInterna = preference.getVersao();

            Log.i("versao interna", versaoInterna);

            Date dataInterna = format.parse(versaoInterna);

            return dataExterna.after(dataInterna);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void buscaTodos(){
        if(preference.temVersao()){
            buscaNovos();
        }else{
            buscaAlunos();
        }
    }

    private void sincronizaAlunosInternos(){
        final AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.listaNaoSincronizados();
        dao.close();

        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().atualiza(alunos);
        call.enqueue(new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                AlunoSync alunoSync = response.body();
                sincroniza(alunoSync);
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {

            }
        });
    }

    private void buscaAlunos() {
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().lista();
        call.enqueue(buscaAlunosCallback());
    }


    private void buscaNovos() {
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().novos(preference.getVersao());
        call.enqueue(buscaAlunosCallback());
    }

    public void deleta(final Aluno aluno) {
        Call<Void> call = new RetrofitInicializador().getAlunoService().deleta(aluno.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                AlunoDAO dao = new AlunoDAO(context);
                dao.deleta(aluno);
                dao.close();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("onDelete", "onFailure: Erro ao remover" );
            }
        });
    }
}