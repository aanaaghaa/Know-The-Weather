package com.example.hp.knowtheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button weatherInfo;
    EditText editText;

    public void knowWeather(View view) {
        try
        {
            DownloadTask downloadTask = new DownloadTask(); //creating the object of DownloadTask class
            downloadTask.execute("https://api.openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() + "&appid=e61bbe932ebddf9e1a4c5c402adca6e2"); //executes the given API URL, by concatenating the City name given by the user

            InputMethodManager inputMethodManager= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //gets the input service of the device
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0); //hides the input service, i.e., soft keyboard, and gets the editText as a token

        }catch(Exception e)
        {e.printStackTrace();
           // textView.setText("Cannot find the city "+ editText.getText());
           textView.setText("Something went wrong"); //prints the error message when there is an exception
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> { //downloads content or loads the content from web and extends AsyncTask

        @Override
        protected String doInBackground(String... urls) {  //predefined method for AsyncTask, Gives an array(pseduo) of strings, in this case url
            URL url;
            String result = "";
            try {
                url = new URL(urls[0]); //url in psuedo
                HttpURLConnection httpsURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
              textView.setText("Something went wrong");
                return null;
            }
        }

       @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");
                String cityInfo=jsonObject.getString("name");
                String windInfo=jsonObject.getString("wind");
                String sysInfo=jsonObject.getString("sys");
                String dateInfo=jsonObject.getString("dt");
                String mainInfo=jsonObject.getString("main");
                String message = "";
                JSONArray jsonArray = new JSONArray(weatherInfo);

                JSONObject windPart=new JSONObject(windInfo);
                JSONObject sysPart=new JSONObject(sysInfo);
               JSONObject tempPart=new JSONObject(mainInfo);
                String temperature=tempPart.getString("temp");
                String country=sysPart.getString("country");
                String speed=windPart.getString("speed");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObjectPart = jsonArray.getJSONObject(i);
                    String main = jsonObjectPart.getString("main");
                    String description = jsonObjectPart.getString("description");

                    if (!main.equals("") && !description.equals("")) {
                        message = main + ": " + description + "\r\n";
                    }

                }
                if (!country.equals(""))
                { message = message+ "Country: " + country + "\n"; }

                if (!cityInfo.equals(""))
                { message =message+ "City: " + cityInfo + "\r\n";}

                Calendar cal = Calendar.getInstance(Locale.getDefault());
                cal.setTimeInMillis(Long.parseLong(dateInfo) * 1000L);
                String date = DateFormat.format("dd-MM-yyyy | hh:mm:ss", cal).toString();

                if (!date.equals(""))
                { message = message+ "Time: " + date + "\r\n";     }

                if (!speed.equals(""))
                { message = message+ "Speed: " + speed + "\r\n"; }

                Double temp=Double.parseDouble(temperature)-273.15;
                int tempValue = (int)Math.round(temp);
                String celsiusTemp=Double.toString(tempValue);
                if (!celsiusTemp.equals(""))
                { message = message+ "Temperature: " + celsiusTemp +"°C" + "\r\n";}

                if (!message.equals(""))
                { textView.setText(message); }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                textView.setText("Something went wrong");
              //  textView.setText("Cannot find the city "+ editText.getText());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.weatherInfo);
        weatherInfo = findViewById(R.id.btn);
        editText=findViewById(R.id.cityName);

    }
}
