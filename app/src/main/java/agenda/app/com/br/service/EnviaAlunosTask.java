package agenda.app.com.br.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import agenda.app.com.br.converter.AlunoConverter;
import agenda.app.com.br.dao.AlunoDAO;
import agenda.app.com.br.modelo.Aluno;

/**
 * Created by massao on 26/02/17.
 *
 * é interessante/melhor usar o Void quando não utiizado nenhum parametro
 */
public class EnviaAlunosTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog dialog;

    public EnviaAlunosTask(Context context){
        this.context = context;
    }

    /**
     * Executa na thread secundaria
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(Void... params) {
        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        AlunoConverter conversor = new AlunoConverter();
        String json = conversor.converteParaJSON(alunos);

        WebClient client = new WebClient();
        String resposta = client.post(json);
        return resposta;
    }

    /**
     * Executado depois da tarefa
     * Sempre executado na thread primária, não a secundária
     * @param resposta
     */
    @Override
    protected void onPostExecute(String resposta) {
        super.onPostExecute(resposta);
        dialog.dismiss();
        Toast.makeText(context, resposta, Toast.LENGTH_SHORT).show();
    }

    /**
     * Executado antes da tarefa
     */
    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde", "Enviando Alunos,,,", true, true);
    }
}
