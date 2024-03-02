package com.example.openweatherapi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private  final String appId = "<YOUR API KEY>";

    DecimalFormat df = new DecimalFormat("#.##");
    EditText editTextCity,editTextCountryCode;
    Button btnGetInfo;
    TextView textViewDisplayResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        editTextCountryCode = findViewById(R.id.editTextCountryCode);
        btnGetInfo = findViewById(R.id.btnGetInfo);
        textViewDisplayResult = findViewById(R.id.textViewDisplayResult);


        btnGetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void getInfo() {
        String tempUrl = "";
        String city = editTextCity.getText().toString().trim();
        String country = editTextCountryCode.getText().toString().trim();
        if(city.equals("")){
            textViewDisplayResult.setText("City field cannot be empty");
        }else{
            if(!country.equals("")){
                tempUrl = url+"?q="+city+","+country+"&appid="+appId;
            }else{
                tempUrl = url+"?q="+city+"&appid="+appId;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST,tempUrl,new Response.Listener<String>(){
                @Override
                public void onResponse(String response){
                    String output = "";
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp")-273.5;
                        double feelsLike = jsonObjectMain.getDouble("feels_like")-273.5;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonObject.getString("name");

                        output += " Current Weather of "+cityName+" ( "+countryName+" ) " +
                                "\n\n Temp : "+df.format(temp)+" celsius"+
                                "\n Feels Like : "+df.format(feelsLike)+" celsius"+
                                "\n Pressure : "+pressure+" hPa"+
                                "\n Humidity : "+humidity+" % "+
                                "\n\n Wind Speed : "+wind+
                                "\n\n Clouds : "+clouds+
                                "\n Description : "+description;
                        textViewDisplayResult.setText(output);
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            },new Response.ErrorListener() {

                @Override
                public  void onErrorResponse(VolleyError error){
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}