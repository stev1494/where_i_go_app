package com.wst.whereigo.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.wst.whereigo.Generic.DirectionsJSONParser;
import com.wst.whereigo.Models.Route;
import com.wst.whereigo.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,GoogleMap.OnMyLocationButtonClickListener  {

    private MapView mapView;
    private GoogleMap mMap;
    TextView tv_route_name;
    Route route;
    Route r2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        //Obtener datos de la ruta
        route = new Gson().fromJson(getIntent().getExtras().getString("route"), Route.class);

        tv_route_name = findViewById(R.id.tv_route_name);
        String head = route.getNumber() + " - " + route.getName();
        tv_route_name.setText(head);

        mapView = (MapView) findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(20);

        LatLng marker = new LatLng(-2.141455, -79.879166);

        View marker_view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_view, null);
        TextView tvMarkerName = (TextView) marker_view.findViewById(R.id.tvMarkerName);

        if(route!=null) {
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
                marker = route.getStations().get(0);
            }
        }

        CameraPosition camera = new CameraPosition.Builder()
                .target(marker)
                .zoom(12)    //limite = 21
                .bearing(0)  // 0 - 365º
                .tilt(0)    //limite = 90
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

        ///Descarga de JSON de Google Directions API
        //DownloadTask downTask = new DownloadTask();
        //downTask.execute("");

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /*Toast.makeText(getContext(), "Click \n Lat: "
                        + latLng.latitude + " \n Long: "
                        + latLng.longitude, Toast.LENGTH_SHORT).show();*/

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /*Toast.makeText(getContext(), "click marker \n Lat: "
                        + marker.getPosition().latitude + " \n Long: "
                        + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();*/
                return true;
            }
        });
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


    /**
     * Tareas asíncronas para obtener la ruta entre dos puntos
     */

    /**Obtener la url para la petición de ruta entre dos puntos*/
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        //Origen
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        //Destino
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        //Habilitar sensor
        String sensor = "sensor=false";

        //Crear parametros para el web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        //Asignar formato de salida
        String output = "json";

        //Crear la url
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /**Descargar el archivo json mediante la url*/
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            //Crear la conexión Http para comunicarse con la url
            urlConnection = (HttpURLConnection) url.openConnection();

            //Conectarse a la url
            urlConnection.connect();

            //Leer datos
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        }catch(Exception e){
            Log.d("Error de descarga", e.toString());
        }finally{
            //Cerrar conexión
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**Clase para obtener datos de la ruta desde la url*/
    private class DownloadTask extends AsyncTask<String, Void, String> {

        //Descarga de datos en segundo plano
        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try{
                //Descargar datos desde el web service
                String urlRoute = getDirectionsUrl(route.getStations().get(8), route.getStations().get(9));
                data = downloadUrl(urlRoute);
            }catch(Exception e){
                Log.d("Descarga datos",e.toString());
            }
            return data;
        }

        //Posterior a la descarga
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //Ejecutar tarea para parsear los datos
            ParserToJSONTask parserTask = new ParserToJSONTask();
            parserTask.execute(result);
        }
    }

    /** Clase para parsear los datos de Google Places a formato JSON */
    private class ParserToJSONTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        //Parseo de datos
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                //Se parsean los datos mediante la clase DirectionsJSONParser, obteniendo la lista de rutas
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            List<LatLng> pointsR = new ArrayList<>();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    pointsR.add(position);

                    points.add(position);
                }

                route.setRoute(pointsR);
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(R.color.colorAccent);

            }

            writeJSON();

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }


    /** Escribir archivo JSON */
    public void writeJSON(){
        //Convertir objeto a JSON
        String StringData   = new Gson().toJson(route);

        //Crear directorio de almacenamiento
        File dir = new File(Environment.getExternalStorageDirectory() + "/Files/");
        if (!dir.exists()) {
            if(!dir.mkdir()){
                Toast.makeText(this, "Error creando el archivo",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //Escribir archivo JSON
        try {
            FileWriter file = new FileWriter(dir.getAbsolutePath()+"/route.json");
            file.write(StringData);
            file.flush();
            file.close();
        }catch (IOException e){
            Log.e("ERROR_JSON", e.getMessage());
        }
    }

    /////___________ EXTRA NO IMPORTANTE

    // Fetches data from url passed
    private class DownloadRoutesTask extends AsyncTask<String, Void, ArrayList<String>> {

        // Downloading data in non-ui thread
        @Override
        protected ArrayList<String> doInBackground(String... url) {

            ArrayList<String> data = new ArrayList<>();

            for (int i=0; i<route.getStations().size()-1;i++){
                String urlRouteDirection="";
                try {
                    Thread.sleep(60000);
                    urlRouteDirection = getDirectionsUrl(route.getStations().get(i), route.getStations().get(i+1));

                } catch (Exception e) {
                    e.getLocalizedMessage();
                }

                try {
                     data.add(downloadUrl(urlRouteDirection));
                }catch (IOException e){
                    Log.e("", e.getMessage());
                }
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);

            ParserRoutesTask parserTask1 = new ParserRoutesTask();

            // Invokes the thread for parsing the JSON data
            parserTask1.execute(result);

        }
    }


    private class ParserRoutesTask extends AsyncTask<ArrayList<String>, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(ArrayList<String>... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            ArrayList<String> jsonString = jsonData[0];
            try{
                for(int i=0; i<jsonString.size();i++){
                    jObject = new JSONObject(jsonString.get(i));
                    DirectionsJSONParser parser = new DirectionsJSONParser();
                    List<List<HashMap<String, String>>> pointsRoute = parser.parse(jObject);
                    if(pointsRoute.size()>0) {
                        routes.addAll(pointsRoute);
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                List<LatLng> pointsR = new ArrayList<>();
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    pointsR.add(position);

                    points.add(position);
                }

                route.setRoute(pointsR);

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(R.color.colorAccent);

            }

            writeJSON();
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
