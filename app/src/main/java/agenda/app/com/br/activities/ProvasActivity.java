package agenda.app.com.br.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import agenda.app.com.br.modelo.Prova;
import agenda.app.com.br.R;

public class ProvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provas);

        //Para usar o fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();

        tx.replace(R.id.frame_principal, new ListaProvaFragment());

        if(estaModoPaisagem())
            tx.replace(R.id.frame_secundario, new DetalhesProvaFragment());

        tx.commit();
    }

    private boolean estaModoPaisagem() {
        return getResources().getBoolean(R.bool.modoPaisagem);
    }

    public void selecionaProva(Prova prova) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(!estaModoPaisagem()){
            FragmentTransaction tx = fragmentManager.beginTransaction();

            DetalhesProvaFragment detalhesFragment = new DetalhesProvaFragment();
            //para passar dados via fragment >> intent
            Bundle params = new Bundle();
            params.putSerializable("prova", prova);
            detalhesFragment.setArguments(params);

            tx.replace(R.id.frame_principal, detalhesFragment);

            //erro no botão de voltar - volta direto para a tela de alunos
            //null serve para marcar em algum pilha em especifica
            tx.addToBackStack(null);

            tx.commit();

        }else{
            DetalhesProvaFragment detalhesFragment =
                    (DetalhesProvaFragment) fragmentManager.findFragmentById(R.id.frame_secundario);
            detalhesFragment.populaCamposCom(prova);
        }
    }
}
