package com.example.cleo.camera;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public String[] lat=new String[100];
    public String[] lon=new String[100];
    public double[] Lat=new double[100];
    public double[] Lon=new double[100];
    private TextView textView;
    private String RetrieveUrl = "http://192.168.43.182/Projects/potholefinal/phone/retrieve_loc.php";
    //private String RetrieveUrl = "http://192.168.43.60/app_db/retrieve_loc.php";
    RequestQueue rq;
    public GoogleMap googleMap;
    int i;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        textView = (TextView) findViewById(R.id.textView);

        rq = Volley.newRequestQueue(this);

        sendJSONRequest();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //mapReady();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }

    public void sendJSONRequest(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RetrieveUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("result");
                    for(i=0; i<jsonArray.length();i++) {
                        JSONObject cos = jsonArray.getJSONObject(i);
                        lat[i] = cos.getString("latitude");
                        lon[i] = cos.getString("longitude");
                        Lat[i] = Double.parseDouble(lat[i]);
                        Lon[i] = Double.parseDouble(lon[i]);
                        drawMarker(new LatLng(Lat[i],Lon[i]));
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY","ERROR");

            }
        }
        );
        rq.add(jsonObjectRequest);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }
    public void drawMarker(LatLng point){
        //textView.append(point.toString());
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        // Adding marker on the Google Map
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(23.12,32.12)));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 9.0f));
    }

}
