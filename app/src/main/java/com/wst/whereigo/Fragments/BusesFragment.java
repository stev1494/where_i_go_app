package com.wst.whereigo.Fragments;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wst.whereigo.Adapters.RoutesAdapter;
import com.wst.whereigo.Models.Route;
import com.wst.whereigo.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusesFragment extends Fragment{


    public BusesFragment() {
        // Required empty public constructor
    }

    RecyclerView rv_routes;
    View rootView;

    MenuItem searchMenuItem;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_buses, container, false);

        Bundle args = getArguments();
        int search = args.getInt("search", 0);
        String query = args.getString("query", "");


        //Preparar lista de autobuses
        rv_routes = rootView.findViewById(R.id.rv_routes);
        rv_routes.setHasFixedSize(false);
        rv_routes.setNestedScrollingEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_routes.setLayoutManager(layoutManager);

        List<Route> routes = new ArrayList<>();

        try{
            //Obtener archivos con las rutas
            String[] routesBuses = getContext().getAssets().list("");
            for(String rb : routesBuses){
                if(rb.endsWith(".json")) {
                    Route route = getRouteJSON(rb);
                    if(search == 0){
                        routes.add(route);
                    }else{
                        if(route.getName().contains(query)||route.getNumber().contains(query)){
                            routes.add(route);
                        }
                    }
                }
            }
        }catch (IOException e){
            Log.e("", e.getMessage());
        }

        //Llenar lista de rutas
        if(routes.size() > 0) {
            RoutesAdapter adp = new RoutesAdapter(routes);
            rv_routes.setAdapter(adp);
        }else{
            Toast.makeText(getContext(), "No hay rutas disponibles", Toast.LENGTH_LONG).show();
        }
        return rootView;
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

}
