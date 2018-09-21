package com.wst.whereigo.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wst.whereigo.Fragments.BusesFragment;
import com.wst.whereigo.Fragments.FindRouteFragment;
import com.wst.whereigo.Fragments.NearRoutesFragment;
import com.wst.whereigo.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    TabLayout.Tab current_tab;
    TextView currentTexViewSelected = null, txtMessage = null;
    Fragment _CurrentFragment = null;
    FrameLayout content = null;
    ImageView  currentImageView;
    TypeView current_view = TypeView.BUS_LIST;
    TabLayout tabLayout;

    MenuItem searchMenuItem;
    SearchView searchView;

    public static final int REQUEST_LOCATION=102;

    GoogleApiClient googleApiClient;

    LocationManager locationManager;
    LocationRequest locationRequest;
    LocationSettingsRequest.Builder locationSettingsRequest;

    PendingResult<LocationSettingsResult> pendingResult;

    private static String[] PERMISSIONS_ALL = {Manifest.permission.ACCESS_FINE_LOCATION}; //TODO You can Add multiple permissions here.
    private static final int PERMISSION_REQUEST_CODE = 223;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
    }

    public void checkPermissions(){

        if( getRequestPermissions() != null){
            ArrayList<String> requestPermissions = getRequestPermissions();
            ActivityCompat.requestPermissions(this,
                    requestPermissions.toArray(new String[requestPermissions.size()]), PERMISSION_REQUEST_CODE);
        }else{
            if(isGpsEnabled()) {
                createMenu();
            }else{
                mEnableGps();
            }
        }
    }

    public void createMenu(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tabLayout = findViewById(R.id.tabs);
        tabLayout.removeAllTabs();

        TabLayout.Tab tab = AddTabCustom(getString(R.string.routes), R.drawable.bus, TypeView.BUS_LIST, tabLayout);
        tab.select();
        AddTabCustom(getString(R.string.near_route), R.drawable.placeholder, TypeView.NEAR_ROUTE, tabLayout);
        AddTabCustom(getString(R.string.find_route), R.drawable.route, TypeView.FIND_MY_ROUTE, tabLayout);
        currentTexViewSelected = tab.getCustomView().findViewById(R.id.tvItemName);
        currentImageView = tab.getCustomView().findViewById(R.id.ivItemImage);

        tabLayout.addOnTabSelectedListener(this);

        ChangeCurrentTab(tab);
        showView(TypeView.BUS_LIST);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ivItemImage) {
            if (currentTexViewSelected != null && currentImageView != null) {
                ChangeCurrentTab((TabLayout.Tab) v.getTag());
            }
        }
    }


    private TabLayout.Tab AddTabCustom(String title, int icon, TypeView tag, TabLayout tabLayout) {
        TabLayout.Tab Item = tabLayout.newTab();
        Item.setTag(tag);
        Item.setCustomView(R.layout.item_menu);
        ((ImageView) Item.getCustomView().findViewById(R.id.ivItemImage)).setImageResource(icon);
        //DrawableCompat.setTint(((ImageView) Item.getCustomView().findViewById(R.id.ivItemImage)).getDrawable(), getResources().getColor(R.color.menuInactive));
        ((TextView) Item.getCustomView().findViewById(R.id.tvItemName)).setText(title);
        Item.getCustomView().findViewById(R.id.tvItemName).setOnClickListener(this);
        Item.getCustomView().findViewById(R.id.tvItemName).setTag(Item);
        tabLayout.addTab(Item);
        return Item;

    }

    private void ChangeCurrentTab(TabLayout.Tab tab) {
        current_tab = tab;
        TextView text = tab.getCustomView().findViewById(R.id.tvItemName);
        ImageView image = tab.getCustomView().findViewById(R.id.ivItemImage);

        currentTexViewSelected = text;
        currentImageView = image;

        if(tab.getTag()== null) return;

        switch ((TypeView) tab.getTag()){
            case BUS_LIST:
                currentTexViewSelected.setTextColor(getResources().getColor(R.color.menu01));
                //DrawableCompat.setTint(currentImageView.getDrawable(), getResources().getColor(R.color.menu01));
                break;
            case NEAR_ROUTE:
                currentTexViewSelected.setTextColor(getResources().getColor(R.color.menu02));
                ///DrawableCompat.setTint(currentImageView.getDrawable(), getResources().getColor(R.color.menu02));
                break;
            case FIND_MY_ROUTE:
                currentTexViewSelected.setTextColor(getResources().getColor(R.color.menu03));
                //DrawableCompat.setTint(currentImageView.getDrawable(), getResources().getColor(R.color.menu03));
                break;
        }
        tab.select();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (currentTexViewSelected != null && currentImageView != null) {
            ChangeCurrentTab(tab);
            showView((TypeView) tab.getTag());
           // txtMessage.setVisibility(View.GONE);
        }
    }

    int search;
    String query = "";
    private void showView(TypeView typeView) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        if (_CurrentFragment != null) {
            ft.remove(_CurrentFragment);
            _CurrentFragment = null;
        }

        current_view =  typeView;

        Fragment fragment;

        switch (typeView) {
            case BUS_LIST:
                fragment = new BusesFragment();
                _CurrentFragment = fragment;

                Bundle bundle = new Bundle();
                bundle.putInt("search", search);
                bundle.putString("query", query);

                fragment.setArguments(bundle);

                search = 0;
                query = "";

                getSupportFragmentManager().beginTransaction().
                        replace(R.id.container, fragment, "BUSES")
                        .commit();

                break;
            case NEAR_ROUTE:
                fragment = new NearRoutesFragment();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.container, fragment, "NEAR_ROUTE")
                        .commit();
                _CurrentFragment = fragment;
                break;

            case FIND_MY_ROUTE:
                fragment = new FindRouteFragment();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.container, fragment, "FIND_ROUTE")
                        .commit();
                _CurrentFragment = fragment;
                break;
        }

        //App.getFragmentStack().push(_CurrentFragment);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        unselectTab(tab);
    }

    public void unselectTab(TabLayout.Tab tab){
        if(tab.getTag()== null) return;
        currentTexViewSelected.setTextColor(getResources().getColor(R.color.menuInactive));
        //DrawableCompat.setTint(currentImageView.getDrawable(), getResources().getColor(R.color.menuInactive));
    }


    public ArrayList<String> getRequestPermissions(){
        ArrayList<String> toReqPermissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean allPermissionsGranted = true;
            for (String permission : PERMISSIONS_ALL) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    toReqPermissions.add(permission);
                    allPermissionsGranted = false;
                }
            }
            if (!allPermissionsGranted){
                return toReqPermissions;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions not granted: " + permissions[i], Toast.LENGTH_LONG).show();
                    allPermGranted = false;
                    finish();
                    break;
                }
            }
            if (allPermGranted)
                if(!isGpsEnabled()){
                    mEnableGps();
                }else{
                    createMenu();
                }
        }
    }


    public boolean isGpsEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return true;

        } else {
            return false;
        }
    }

    public void mEnableGps() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        mLocationSetting();
    }

    public void mLocationSetting() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);
        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        mResult();
    }

    public void mResult() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    //showView(TypeView.SOLO_PARA_TI);

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(this, "Gps enabled", Toast.LENGTH_SHORT).show();
                        createMenu();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this,"Es necesario habilitar el GPS para usar la aplicación",Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem search_item = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint("Buscar");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {

                search = 1;
                query = s;

                if(tabLayout.getTabAt(0).isSelected()){
                    showView(TypeView.BUS_LIST);
                }else{
                    tabLayout.getTabAt(0).select();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                search = 0;
                query = "";
                if(tabLayout.getTabAt(0).isSelected()){
                    showView(TypeView.BUS_LIST);
                }else{
                    tabLayout.getTabAt(0).select();
                }
                return false;
            }
        });

        return  true;
    }


    private enum TypeView {
        BUS_LIST,
        NEAR_ROUTE,
        FIND_MY_ROUTE
    }

}
