package com.wst.whereigo.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.wst.whereigo.Models.Route;
import com.wst.whereigo.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NearRoutesFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,GoogleMap.OnMyLocationButtonClickListener,
LocationListener{

    private MapView mapView;
    private GoogleMap mMap;
    View rootView;

    LatLng userLocation = null;

    public NearRoutesFragment() {
        // Required empty public constructor
    }

    SeekBar seekBar;
    TextView tv_km;
    int KmNear = 500;   //Distancia predeterminada
    List<Route> routes;

    LocationListener locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_near_routes, container, false);
        tv_km = rootView.findViewById(R.id.tv_km_near);
        seekBar = rootView.findViewById(R.id.seekBar);

         routes = new ArrayList<>();

        try{
            //Obtener archivos con las rutas
            String[] routesBuses = getContext().getAssets().list("");
            for(String rb : routesBuses){
                if(rb.endsWith(".json")) {
                    Route route = getRouteJSON(rb);
                    routes.add(route);
                }
            }
        }catch (IOException e){
            Log.e("", e.getMessage());
        }

        seekBar.setEnabled(false);
        seekBar.setProgress(KmNear);
        String mts = String.valueOf(KmNear)+" mts";
        tv_km.setText(mts);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String meters = String.valueOf(progress) + " mts";
                tv_km.setText(meters);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Se actualiza el la distancia
                KmNear = seekBar.getProgress();

                mMap.clear();
                displayNearRoutes();
            }
        });

        mapView = (MapView) rootView.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        return rootView;
    }

    /*Mostrar las rutas más cercanas en el mapa*/
    public void displayNearRoutes(){
        List<Route> nearRoutes = getNearRoutes();
        for(Route route: nearRoutes){

            //Vista del marcador
            View marker_view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_view, null);
            TextView tvMarkerName = (TextView) marker_view.findViewById(R.id.tvMarkerName);


            if(route.getRoute().size()>0) {

                Random random = new Random();
                int color = Color.parseColor("#3f91d4");

                if(nearRoutes.size()>1) {
                    color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                }

                //Dibujar ruta
                PolylineOptions p = new PolylineOptions();
                for (LatLng l : route.getRoute()) {
                    p.add(l);
                }
                p.color(color);
                p.width(8.0f);
                Polyline line = mMap.addPolyline(p);
            }
            if(route.getStations().size()>0){
                int index = 0;
                //Dibujar marcadores de las estaciones
                for(LatLng l : route.getStations()) {
                    tvMarkerName.setText(getStationName(index));
                    mMap.addMarker(new MarkerOptions()
                            .position(l)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(marker_view)))
                    );
                    index++;
                }
            }
        }
        if( nearRoutes.size()==0){
            Toast.makeText(getContext(), "No se encontraron rutas cercanas", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), nearRoutes.size() + " rutas cercanas", Toast.LENGTH_SHORT).show();
        }
        CameraPosition camera = new CameraPosition.Builder()
                .target(userLocation)
                .zoom(12)    //limite = 21
                .bearing(0)  // 0 - 365º
                .tilt(0)    //limite = 90
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    /*Obtener la posición actual*/
    public LatLng getCurrentLocation(){
        LatLng location = null;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager  = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            location = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }
        return  location;
    }

    /* Encontrar las rutas más cercanas*/
    public List<Route> getNearRoutes(){

        List<Route> nearRoutes = new ArrayList<>();
        float[] results = new float[1];

        userLocation = getCurrentLocation(); //new LatLng(-2.180201, -79.945420);//

        for(Route route: routes){
            for(LatLng station: route.getStations()){

                //Obtener distancias entre dos puntos
                Location.distanceBetween(station.latitude, station.longitude, userLocation.latitude, userLocation.longitude,results);

                if(results[0]<KmNear){
                    nearRoutes.add(route);
                    break;
                }
            }
        }
        return nearRoutes;
    }

    //Obtener JSON de la ruta y crear un objeto Route
    public Route getRouteJSON(String filename){
        Route route = null;
        String json = null;
        try {
            InputStream is = getContext().getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            route = new Gson().fromJson(json, Route.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return route;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(15);

        seekBar.setEnabled(true);

        displayNearRoutes();

    }

    /* Definir etiquetas para las estaciones */
    public String getStationName(int index){
        switch (index){
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            case 8:
                return "I";
            case 9:
                return "J";
            case 10:
                return "K";
            case 11:
                return "L";
            case 12:
                return "M";
            case 13:
                return "N";
            case 14:
                return "O";
            case 15:
                return "P";
            case 16:
                return "Q";
            case 17:
                return "R";
            case 18:
                return "S";
            case 19:
                return "T";
            case 20:
                return "U";
            case 21:
                return "V";
            case 22:
                return "W";
            case 23:
                return "X";
            case 24:
                return "Y";
            case 25:
                return "Z";
            default:
                return String.valueOf(index);
        }
    }

    //Crear icono para la vista del marcador
    private Bitmap getMarkerBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onLocationChanged(Location location) {
        //mMap.clear();
        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
