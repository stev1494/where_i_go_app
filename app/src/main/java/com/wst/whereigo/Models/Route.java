package com.wst.whereigo.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route {
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Number")
    @Expose
    private String number;
    @SerializedName("Route")
    @Expose
    private List<LatLng> route;
    @SerializedName("Stations")
    @Expose
    private List<LatLng> stations;

    public Route(String name) {
        this.name = name;
    }

    public Route(String name, List<LatLng> stations) {
        this.name = name;
        this.stations = stations;
    }

    public List<LatLng> getStations() {
        return stations;
    }

    public void setStations(List<LatLng> stations) {
        this.stations = stations;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void setRoute(List<LatLng> route) {
        this.route = route;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
