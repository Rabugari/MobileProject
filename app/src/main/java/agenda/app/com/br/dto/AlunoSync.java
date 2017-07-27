package agenda.app.com.br.dto;

import java.util.List;

import agenda.app.com.br.modelo.Aluno;

/**
 * Created by Massao on 18/07/2017.
 */

public class AlunoSync {

    private List<Aluno> alunos;
    private String momentoDaUltimaModificacao;

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

    public String getMomentoDaUltimaModificacao() {
        return momentoDaUltimaModificacao;
    }

    public void setMomentoDaUltimaModificacao(String momentoDaUltimaModificacao) {
        this.momentoDaUltimaModificacao = momentoDaUltimaModificacao;
    }
}
