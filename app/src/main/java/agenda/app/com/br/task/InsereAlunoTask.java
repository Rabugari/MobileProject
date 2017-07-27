package agenda.app.com.br.task;

import android.os.AsyncTask;

import agenda.app.com.br.converter.AlunoConverter;
import agenda.app.com.br.modelo.Aluno;
import agenda.app.com.br.service.WebClient;

/**
 * Created by Massao on 18/07/2017.
 */

public class InsereAlunoTask extends AsyncTask {

    private final Aluno aluno;

    public InsereAlunoTask(Aluno aluno) {
        this.aluno = aluno;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String json = new AlunoConverter().converteParaJSONCompleto(aluno);
        new WebClient().insere(json);
        return null;
    }
}
