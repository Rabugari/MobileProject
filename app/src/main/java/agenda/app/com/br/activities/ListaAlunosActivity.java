package agenda.app.com.br.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import agenda.app.com.br.adapter.AlunoAdapter;
import agenda.app.com.br.dao.AlunoDAO;
import agenda.app.com.br.events.AtualizaListaAlunoEvent;
import agenda.app.com.br.modelo.Aluno;
import agenda.app.com.br.service.EnviaAlunosTask;
import agenda.app.com.br.sinc.AlunoSincronizador;
import agenda.app.com.br.R;

public class ListaAlunosActivity extends AppCompatActivity {

    private final AlunoSincronizador sincronizador = new AlunoSincronizador(this);
    private ListView listView;
    private SwipeRefreshLayout swipe;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        //para atualizar a lista quando receber um novo aluno (AgendaMessagingService)
        eventBus = EventBus.getDefault();
        //mass... temos que inscrever no metodo - atualizaListaAlunoEvent via @Subscribe
        eventBus.register(this);

        listView = (ListView) findViewById(R.id.lista_alunos);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Aluno aluno = (Aluno) listView.getItemAtPosition(i);

                Intent intent = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intent.putExtra("aluno", aluno);
                startActivity(intent);

                //Toast.makeText(ListaAlunosActivity.this, "Aluno " + aluno.getNome(), Toast.LENGTH_SHORT).show();
            }
        });

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_lista_aluno);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sincronizador.buscaTodos();
            }
        });

        Button btnNovoAluno = (Button) findViewById(R.id.novo_aluno);
        btnNovoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListaAlunosActivity.this, FormularioActivity.class));
            }
        });

        registerForContextMenu(listView);

        //busca todos os alunos
        sincronizador.buscaTodos();
    }


    //mas não podemos alterar a view, se a thread não está na activity, pois aqui esta sendo chamado via
    //eventbus, então vamos executar na thread principal
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void atualizaListaAlunoEvent(AtualizaListaAlunoEvent event){
        //desliga o loading
        if(swipe.isRefreshing())
            swipe.setRefreshing(false);
        carregaLista();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        carregaLista();
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        eventBus.unregister(this);
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listView.getItemAtPosition(info.position);

        MenuItem menuLigar = menu.add("Ligar");
        menuLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                }else{
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });


        MenuItem viewSite = menu.add("Visualizar Site");

        String site = aluno.getSite();

        if(!site.startsWith("http://"))
            site = "http://" + site;

        Intent siteIntent = new Intent(Intent.ACTION_VIEW);
        siteIntent.setData(Uri.parse(site));
        viewSite.setIntent(siteIntent);

        MenuItem menuSms = menu.add("Enviar SMS");
        Intent intentSms = new Intent(Intent.ACTION_VIEW);
        intentSms.setData(Uri.parse("sms:" + aluno.getTelefone()));
        menuSms.setIntent(intentSms);

        MenuItem menuMap = menu.add("Visualizar no mapa");
        Intent intentMap = new Intent(Intent.ACTION_VIEW);
        intentMap.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        menuMap.setIntent(intentMap);

        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Toast.makeText(ListaAlunosActivity.this, "Deletando aluno: " + aluno.getNome(), Toast.LENGTH_SHORT).show();

                AlunoDAO alunoDAO = new AlunoDAO(ListaAlunosActivity.this);
                alunoDAO.deleta(aluno);
                alunoDAO.close();
                carregaLista();

                sincronizador.deleta(aluno);
                return false;
            }
        });
    }

    private void carregaLista() {
        AlunoDAO alunoDAO = new AlunoDAO(this);
        List<Aluno> alunos = alunoDAO.buscaAlunos();

        for (Aluno aluno : alunos){
            Log.i("sincronizado", "sincronizado: " + String.valueOf(aluno.getSincronizado()));
        }

        alunoDAO.close();

        AlunoAdapter adapter = new AlunoAdapter(this, alunos);

        //ArrayAdapter<Aluno> adapter = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunos);
        listView.setAdapter(adapter);
    }

    //retorno do request permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 123){
            //faz a ligação
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_enviar_notas:{
                new EnviaAlunosTask(this).execute();
                break;
            }
            case R.id.menu_baixar_provas:{
                Intent intentProvas = new Intent(this, ProvasActivity.class);
                startActivity(intentProvas);
                break;
            }
            case R.id.menu_mapa:{
                Intent intentMapa = new Intent(this, MapaActivity.class);
                startActivity(intentMapa);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
