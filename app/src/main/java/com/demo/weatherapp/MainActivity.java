package com.demo.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMuxer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL = "https://samples.openweathermap.org/data/2.5/weather?q=%s&appid=439d4b804bc8187953eb36d2a8c26a02";
    private EditText editTextCity;
    private TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewWeather);
    }

    public void onClickShowWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if(!city.isEmpty()) {
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHER_URL, city);
            task.execute(url);
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override //установка текста у textView при помощи переопределения этого метода
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format("%s\nТемпература: %s\nНа улице: %s", city, temp, description);
                textViewWeather.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
