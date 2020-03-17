package com.example.whattheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultText;

    public void findWeather(View view) {

        Log.i("cityName",cityName.getText().toString());

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        DownloadTask task = new DownloadTask();
        String site = "https://samples.openweathermap.org/data/2.5/weather?q="+cityName.getText().toString()+"&appid=b6907d289e10d714a6e88b30761fae22";
        task.execute(site);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        resultText = findViewById(R.id.resultText);
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            String  result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            
            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char)data;

                    result += current;

                    data = reader.read();
                }

                return result;
            }catch (MalformedURLException e) {
                e.printStackTrace();
                return "Failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                String weather = jsonObject.getString("weather");

                Log.i("Website Content","" +weather);

                JSONArray arr = new JSONArray(weather);

                for (int i = 0 ; i < arr.length() ; i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != "") {

                        message += main +": " + description + "\r\n";

                    }
                }

                if(message != "") {

                    resultText.setText(message);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
