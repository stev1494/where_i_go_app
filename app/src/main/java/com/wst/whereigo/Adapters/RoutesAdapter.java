package com.wst.whereigo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wst.whereigo.Models.Route;
import com.wst.whereigo.R;
import com.wst.whereigo.Activities.RouteActivity;

import java.util.List;

/**
 * Adaptador para la lista de buses
 */
public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RoutesHolder> {
    int color;
    public class RoutesHolder extends RecyclerView.ViewHolder {
        TextView tv_route_name;
        ImageView iv_route_icon;
        TextView tv_route_number;
        ConstraintLayout item_layout;
        int viewType = -1;

        //Preparación de UI
        public RoutesHolder(View view) {
            super(view);
            tv_route_name = view.findViewById(R.id.tv_route_name);
            iv_route_icon = view.findViewById(R.id.iv_route_icon);
            tv_route_number = view.findViewById(R.id.tv_route_number);
            item_layout = view.findViewById(R.id.item_layout);

        }
    }

    private List<Route> routes;

    private Context context;
    ViewGroup mParent;

    int B1 = 0;
    int B2 = 1;
    int B3 = 2;

    int REQUEST_PHONE_CALL = 0;

    public RoutesAdapter(List<Route> _routers) {
        this.routes = _routers;
    }

    @Override
    public RoutesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Preparar vista del item
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_route2, parent, false);
        RoutesHolder pvh = new RoutesHolder(v);
        pvh.viewType = viewType;
        context = parent.getContext();
        return pvh;
    }

    @Override
    public void onBindViewHolder(final RoutesHolder holder, final int position) {
        //Mostrar nombre de la ruta
        holder.tv_route_name.setText(routes.get(position).getName());
        holder.tv_route_number.setText(routes.get(position).getNumber());

        //Mostrar icono de la ruta
        if(getItemViewType(position)==B1){
            holder.iv_route_icon.setImageResource(R.drawable.bus03);
        }else if (getItemViewType(position)==B2){
            holder.iv_route_icon.setImageResource(R.drawable.bus05);
        }else{
            holder.iv_route_icon.setImageResource(R.drawable.bus04);
        }

        //Listener para seleccionar elemento de la lista
        holder.item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Convertir objeto Route seleccionado a JSON
                String routeData   = new Gson().toJson(routes.get(position));
                //Cargar activity de la ruta
                Intent intent = new Intent(context, RouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("route", routeData);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        int i = position%3;

        if (i==0) {
            return B1;
        } else if(i==1){
            return B2;
        }else{
            return B3;
        }
    }


    @Override
    public int getItemCount() {
        return routes.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
