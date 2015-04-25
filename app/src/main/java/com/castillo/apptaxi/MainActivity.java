package com.castillo.apptaxi;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
    public static Context context;

    String Latitud ="";
    String Longitud = "";
    String idCarrera="";
    String NoTaxi="";



    private LocationListener locListener;

    public static Context getAppContext()
    {
        return context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        VolleySingleton vs = VolleySingleton.getInstance();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public JsonObjectRequest jorsolicitartaxi (){
        String url = "http://getataxi.webege.com/carreras_agr.php?Latitud="+Latitud+"&Longitud="+Longitud;
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                     idCarrera = response.optString("IdCarrera");
                   // ((TextView)findViewById(R.id.textView)).setText(jobid);


            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        return jor;

    }

    public JsonObjectRequest jorsolicitarnotaxi (){
        String url = "http://getataxi.webege.com/carreras_notaxi.php?idCarrera="+idCarrera;
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                NoTaxi = response.optString("NoTaxi");
                if (!NoTaxi.equals("0")) {
                    ((TextView) findViewById(R.id.textView)).setText(NoTaxi);
                }else {
                    NoTaxi="";
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        return jor;

    }



    private static Criteria searchProviderCriteria = new Criteria();

    // Location Criteria
    static {
        searchProviderCriteria.setPowerRequirement(Criteria.POWER_LOW);
        searchProviderCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        searchProviderCriteria.setCostAllowed(false);
    }




    private void comenzarLocalizacion()
    {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locManager.getBestProvider(searchProviderCriteria, true);
        Location loc = locManager.getLastKnownLocation(provider);
        mostrarPosicion(loc);

        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mostrarPosicion(location);
            }
            public void onProviderDisabled(String provider){



            }
            public void onProviderEnabled(String provider){

            }
            public void onStatusChanged(String provider, int status, Bundle extras){
                Log.i("", "Provider Status: " + status);

            }
        };

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locListener);
    }




    private void mostrarPosicion(Location loc) {
        if(loc != null)
        {
            Latitud= String.valueOf(loc.getLatitude());
            Longitud=String.valueOf(loc.getLongitude());

        }

    }

    public void llamaruntaxi(View v){
        comenzarLocalizacion();
        this.context = getApplicationContext();
        VolleySingleton vs = VolleySingleton.getInstance();
        if (!Latitud.isEmpty()&&!Longitud.isEmpty()){
            if(idCarrera.isEmpty()){
                vs.getRequestQueue().add(jorsolicitartaxi());
                Toast toast = Toast.makeText(context, "Solicitud Enviada",Toast.LENGTH_SHORT);
                toast.show();
                solicitudTaxi();
            }else {
                solicitudTaxi();
                Toast toast = Toast.makeText(context, "Esperando Respuesta",Toast.LENGTH_SHORT);
                toast.show();

            }
        }else
        {
            Toast toast = Toast.makeText(context, "Habilite el GPS de su Dispositivo",Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void solicitudTaxi(){
        this.context = getApplicationContext();
        VolleySingleton vs = VolleySingleton.getInstance();
        vs.getRequestQueue().add(jorsolicitarnotaxi());
        /*while (NoTaxi.isEmpty()) {
            try {
                Thread.sleep(20000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            vs.getRequestQueue().add(jorsolicitarnotaxi());
        }*/


    }








}
