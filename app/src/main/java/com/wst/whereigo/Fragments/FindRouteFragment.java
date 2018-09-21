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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.wst.whereigo.Generic.PlaceArrayAdapter;
import com.wst.whereigo.Models.Route;
import com.wst.whereigo.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindRouteFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private MapView mapView;
    private GoogleMap mMap;
    View rootView;

    private static final String TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView act_origin;
    private AutoCompleteTextView act_destination;

    private TextView mNameView;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceOriginArrayAdapter;
    private PlaceArrayAdapter mPlaceDestArrayAdapter;

    LatLng origin = null;
    LatLng dest = null;

    // Zona límite para encontrar lugares
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(-2.284626, -79.958307), new LatLng(-2.016091, -79.874274));

    public FindRouteFragment() {
        // Required empty public constructor
    }

    List<Route> routes;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_find_route, container, false);

        //Obtener todas las rutas
        routes = getRoutes();

        mapView = rootView.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        act_origin = rootView.findViewById(R.id.act_origen);
        act_origin.setThreshold(3);

        act_destination = rootView.findViewById(R.id.act_destino);
        act_destination.setThreshold(3);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        //Establecer adaptador para las editText de autocompletado
        act_origin.setOnItemClickListener(mActOriginClickListener);
        act_destination.setOnItemClickListener(mActDestinationClickListener);

        mPlaceOriginArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mPlaceDestArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        act_origin.setAdapter(mPlaceOriginArrayAdapter);
        act_destination.setAdapter(mPlaceDestArrayAdapter);
        return rootView;
    }

    /* Listener para seleccionar el lugar de origen */
    private AdapterView.OnItemClickListener mActOriginClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceOriginArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceOriginDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    /* Listener para seleccionar el lugar de destino */
    private AdapterView.OnItemClickListener mActDestinationClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceDestArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDestDetailsCallback);
        }
    };

    /* Obtiene la información del lugar de origen seleccionado */
    private ResultCallback<PlaceBuffer> mUpdatePlaceOriginDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }

            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            origin = place.getLatLng();
            displayRoute();
        }
    };

    /* Obtiene la información del lugar de destino seleccionado */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDestDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            dest = place.getLatLng();
            displayRoute();
        }
    };

    /* Mostrar la ruta cercana a ambas posiciones */
    public void displayRoute(){
        mMap.clear();

        //Dibujar marcador en el origen
        if(origin!=null) {
            mMap.addMarker(new MarkerOptions()
                    .position(origin)
                    .title("Origen")
            );
        }

        //Dibujar marcador en el destino
        if(dest!=null) {
            mMap.addMarker(new MarkerOptions()
                    .position(dest)
                    .title("Destino")
            );
        }

        //Dibujar ruta más próxima
        if(origin!=null && dest!=null){

            //Obtener la ruta más próxima
            Route route = getNearestRoute();

            if(route == null){
                Toast.makeText(getContext(), "No exiten rutas próximas", Toast.LENGTH_LONG).show();
                return;
            }
            View marker_view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_view, null);
            TextView tvMarkerName = (TextView) marker_view.findViewById(R.id.tvMarkerName);

            if(route.getRoute().size()>0) {
                PolylineOptions p = new PolylineOptions();
                for (LatLng l : route.getRoute()) {
                    p.add(l);
                }
                p.color(R.color.colorAccent);
                p.width(6.0f);
                Polyline line = mMap.addPolyline(p);
            }
            if(route.getStations().size()>0){
                int index = 0;
                for(LatLng l : route.getStations()) {
                    tvMarkerName.setText(getStationName(index));
                    mMap.addMarker(new MarkerOptions()
                            .position(l)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(marker_view)))
                    );
                    index++;
                }
            }

            Toast.makeText(getContext(), "La ruta más próxima es la " + route.getName(), Toast.LENGTH_LONG).show();
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


    /* Obtiene la ruta más próxima al origen y destino*/
    public Route getNearestRoute(){
        float minDistance = 200f;  //Mínimo de proximidad
        float maxDistance = 3000f; //Máximo de proximidad

        Route nearestRoute = null;

        List<Route> nearRoutesToDest = new ArrayList<>();

        float[] results = new float[1];

        //Se obtienen las rutas más cercanas al destino
        for(Route route: routes){
            for(LatLng station: route.getStations()){
                Location.distanceBetween(station.latitude, station.longitude, dest.latitude, dest.longitude,results);

                if(results[0] <= minDistance){
                    nearRoutesToDest.add(route);
                    break;
                }
            }
        }

        //De las rutas más cercanas al destino, se obtiene la más cercana al origen
        for(Route route: nearRoutesToDest){
            for(LatLng station: route.getStations()){
                Location.distanceBetween(station.latitude, station.longitude, origin.latitude, origin.longitude,results);

                if(results[0] <= maxDistance){
                    maxDistance = results[0];
                    nearestRoute = route;
                }
            }
        }

        return nearestRoute;
    }


    public List<Route> getRoutes(){
        List<Route> routes = new ArrayList<>();

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

        return routes;
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
    public void onConnected(@Nullable Bundle bundle) {

        mPlaceOriginArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceDestArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceOriginArrayAdapter.setGoogleApiClient(null);
        mPlaceDestArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getContext(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();

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
        mMap.setMinZoomPreference(5);
        mMap.setMaxZoomPreference(15);

        LatLng marker = BOUNDS_MOUNTAIN_VIEW.getCenter();

        CameraPosition camera = new CameraPosition.Builder()
                .target(marker)
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }


}
