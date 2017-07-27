package agenda.app.com.br.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import agenda.app.com.br.modelo.Prova;
import agenda.app.com.br.R;

/**
 * Created by massao on 26/02/17.
 */

public class ListaProvaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_provas, container, false);

        List<String> topicosPort = Arrays.asList("Sujeito", "Objeto Direto", "Objeto Indireto");
        Prova provaPortugues = new Prova("Português", "25/05/2017", topicosPort);

        List<String> topicosMat = Arrays.asList("Equações de Segundo Grau", "Trigonometria");
        Prova provaMatematica = new Prova("Matemática", "26/05/2017", topicosMat);

        List<Prova> provas = Arrays.asList(provaPortugues, provaMatematica);

        ArrayAdapter<Prova> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, provas);

        ListView lista = (ListView) view.findViewById(R.id.provas_lista);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prova prova = (Prova) parent.getItemAtPosition(position);

                //continua preso :/
                ProvasActivity provasActivity = (ProvasActivity) getActivity();
                provasActivity.selecionaProva(prova);

                //Para "atualizar o fragment" - ruim, depdende de uma activity
                /*
                FragmentManager manager = getFragmentManager();
                FragmentTransaction tx = manager.beginTransaction();
                tx.replace(R.id.frame_principal, new DetalhesProvaFragment());
                tx.commit();
                */

                /*
                Intent detalhesProva = new Intent(getContext(), DetalhesProvaActivity.class);
                detalhesProva.putExtra("prova", prova);
                startActivity(detalhesProva);
                */
                //Toast.makeText(ProvasActivity.this, "Clicou na prova de " + prova, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
