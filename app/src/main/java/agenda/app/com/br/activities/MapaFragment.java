package agenda.app.com.br.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import agenda.app.com.br.Localizador;
import agenda.app.com.br.dao.AlunoDAO;
import agenda.app.com.br.modelo.Aluno;

/**
 * Created by massao on 26/02/17.
 */

public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mapa;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mapa = googleMap;

        LatLng posicao = pegaCoordenadaDoEndereco("Avenida Antônio Frederico Ozanan, 9500, Jundiaí");

        if(posicao!=null){
            centralizaEm(posicao);
        }

        AlunoDAO alunoDAO = new AlunoDAO(getContext());
        for(Aluno aluno : alunoDAO.buscaAlunos()){
            LatLng coordenada = pegaCoordenadaDoEndereco(aluno.getEndereco());
            if(coordenada!=null){
                MarkerOptions marcador =  new MarkerOptions();
                marcador.position(coordenada);
                marcador.title(aluno.getNome());
                marcador.snippet("Nota: " + aluno.getNota());
                googleMap.addMarker(marcador);
            }
        }
        alunoDAO.close();

//        /new Localizador(getContext(), googleMap);
    }

    private LatLng pegaCoordenadaDoEndereco(String endereco){
        Geocoder geocoder = new Geocoder(getContext());
        //1 para qtde resultados
        try {
            List<Address> resultados = geocoder.getFromLocationName(endereco, 1);

            if(!resultados.isEmpty()){
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(),
                        resultados.get(0).getLongitude());
                return posicao;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void centralizaEm(LatLng coordenada) {
        if(mapa!=null){
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(coordenada, 17);
            mapa.moveCamera(update);
        }
    }
}
