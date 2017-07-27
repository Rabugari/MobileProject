package agenda.app.com.br;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import agenda.app.com.br.activities.MapaFragment;

/**
 * Created by massao on 26/02/17.
 * Funcionalidade de gps
 */

public class Localizador implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleApiClient client;
    private Context context;
    private MapaFragment mapa;

    public Localizador(Context context, MapaFragment mapa){
        client = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        client.connect();

        this.context = context;
        this.mapa = mapa;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();
        //deslocamento minimo para procurar novas posições
        //para o gps atualizar
        request.setSmallestDisplacement(50);

        //intervalo entre os request
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng coordenada = new LatLng(location.getLatitude(), location.getLongitude());
        mapa.centralizaEm(coordenada);
    }
}

